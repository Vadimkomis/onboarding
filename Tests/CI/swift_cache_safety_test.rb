#!/usr/bin/env ruby
# frozen_string_literal: true

require "yaml"

repository_root = File.expand_path("../..", __dir__)
workflow_path = File.join(repository_root, ".github/workflows/ci.yml")
workflow = YAML.load_file(workflow_path)
swift_job = workflow.fetch("jobs").fetch("test")
job_env = swift_job.fetch("env", {})
invalid_runner_env = job_env.find do |_name, value|
  value.to_s.match?(/\$\{\{\s*runner\./)
end

if invalid_runner_env
  abort("Swift job env cannot use runner context: #{invalid_runner_env.first}")
end

steps = swift_job.fetch("steps")
cache_steps = steps.select do |step|
  step["uses"]&.start_with?("actions/cache@")
end
safety_step = steps.find do |step|
  step["run"]&.include?("ruby Tests/CI/swift_cache_safety_test.rb")
end
swift_test_step = steps.find do |step|
  step["run"]&.include?("xcodebuild test")
end

abort("Swift CI must define a dependency cache step") if cache_steps.empty?
abort("Swift CI must run the cache safety regression test") unless safety_step
abort("Swift CI must define an xcodebuild test step") unless swift_test_step
abort("Cache safety must run before cache restore") unless steps.index(safety_step) < steps.index(cache_steps.first)

derived_data_path = swift_test_step.fetch("env").fetch("DERIVED_DATA_PATH")

cached_paths = cache_steps.flat_map do |cache_step|
  cache_step.fetch("with").fetch("path").lines.map(&:strip).reject(&:empty?)
end
resolve_path = lambda do |path|
  expanded = path.sub(/\A\$\{\{\s*runner\.temp\s*\}\}/, "/runner-temp")
  File.expand_path(expanded, repository_root)
end
derived_data_path = resolve_path.call(derived_data_path)
unsafe_path = cached_paths.find do |cached_path|
  cached_path = resolve_path.call(cached_path)
  derived_data_path == cached_path || derived_data_path.start_with?("#{cached_path}/")
end

abort("Swift CI must not cache DerivedData through #{unsafe_path}") if unsafe_path

puts "Swift CI cache does not include DerivedData"
