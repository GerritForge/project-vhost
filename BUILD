load("//tools/bzl:junit.bzl", "junit_tests")
load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "virtualhost",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Implementation-Title: Gerrit Virtual Host plugin",
        "Implementation-URL: https://github.com/gerritforge/gerrit-virtualhost",
        "Gerrit-PluginName: virtualhost",
        "Gerrit-Module: com.gerritforge.gerrit.plugins.vhost.Module",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

junit_tests(
    name = "tests",
    srcs = glob(["src/test/java/**/*Test.java"]),
    visibility = ["//visibility:public"],
    deps = [
        ":project-vhost__plugin",
        "//gerrit-acceptance-framework:lib",
        "//gerrit-plugin-api:lib",
    ],
)
