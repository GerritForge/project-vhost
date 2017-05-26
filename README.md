# Gerrit VirtualHost

Gerrit lib module to split the projects' space into virtual hosts
similarly of what you would do with an HTTP Server and different
domain names.

## How to build

Build this module as it was a Gerrit plugin:

- Clone Gerrit source tree
- Link the virtualhost directory to /plugins
- Run 'bazel build plugins/virtualhost'

## How install

Copy the virtualhost.jar library to /libs and add the following
two extra settings to gerrit.config:

- gerrit.installModule = com.gerritforge.gerrit.modules.virtualhost.GuiceModule
- httpd.filterClass = com.gerritforge.gerrit.modules.virtualhost.VirtualHostFilter

## How to define virtual hosts

/etc/virtualhost.config contains the definition of the virtual
hosts and the set of projects included.

Each ```server``` section defines a virtual host and contains a set of projects
included. Projects are defined using Gerrit ref-matching expressions and can
be repeated multiple times to include multiple matchers.

Example to include all the projects starting with ```team1/``` and the ones
starting with the username:

```
[server "team1.mycompany.com"]
  projects = team1/*
  projects = ${username}/*
```

## Default host

For all the other server names that are not defined and for SSH access, there
is a special default section that lists of visible projects.

Example to include all the projects by default:

```
[default]
  projects = ^.*
```
