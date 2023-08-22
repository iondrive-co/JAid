package jaid;

import rife.bld.BaseProject;
import rife.bld.operations.JUnitOperation;

import java.util.List;

import static rife.bld.dependencies.Repository.MAVEN_CENTRAL;
import static rife.bld.dependencies.Scope.compile;
import static rife.bld.dependencies.Scope.test;

public class Builder extends BaseProject {
    public Builder() {
        pkg = "jaid";
        name = "JAid";
        mainClass = "jaid.Launcher";
        version = version(0,1,0);

        downloadSources = true;
        repositories = List.of(MAVEN_CENTRAL);
        scope(compile)
        .include(dependency("org.apache.commons", "commons-math3", version(3,6,1)))
        .include(dependency("it.unimi.dsi", "fastutil", version(8, 5, 12)))
        .include(dependency("com.google.guava", "guava", version("31.1-jre")))
        .include(dependency("org.slf4j", "slf4j-api", version(2, 0, 7)))
        .include(dependency("org.slf4j", "log4j-over-slf4j", version(2, 0, 7)));

        scope(test)
        .include(dependency("org.assertj", "assertj-core", version(3, 24, 2)))
        .include(dependency("org.junit.jupiter", "junit-jupiter", version(5,9,2)))
        .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1,9,2)))
        .include(dependency("org.openjdk.jmh", "jmh-core", version(1, 37)))
        .include(dependency("org.openjdk.jmh", "jmh-generator-annprocess", version(1, 37)));
    }

    private final JUnitOperation junitTestOperation_ = new JUnitOperation();

    @Override
    public JUnitOperation testOperation() {
        return junitTestOperation_;
    }

    public static void main(String[] args) {
        new Builder().start(args);
    }
}