package jaid;

import rife.bld.BaseProject;

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
        .include(dependency("junit", "junit", version(4, 13, 2)))
        .include(dependency("org.openjdk.jmh", "jmh-core", version(1, 37)))
        .include(dependency("org.openjdk.jmh", "jmh-generator-annprocess", version(1, 37)));

    }

    public static void main(String[] args) {
        new Builder().start(args);
    }
}