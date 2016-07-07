# lein-codox-rsync

Leiningen plugin to release codox documenation with rsync

## Usage

Include the plugin in the `[:user :plugins]` of your
`~/.lein/profiles.clj` configuration file or your `project.clj` file.

``` clojure
{:user {:plugins [[lein-codox-rsync "0.1.0"]]}}
```

Configure the following options under the `[:user :codox :rsync]` key
of your `~/.lein/profiles.clj` configuration file or in your
`project.clj` file:

``` clojure
{:user {:codox {:rsync {:remote-path ""
                        :remote-user "" ;; Optional (Default: Current user)
                        :remote-host "" ;; Optional (Default: Local machine)
                        }}}
```

Then run:

```sh
lein codox-rsync
```

## Requirements

- `rsync` > version 2.6.7

## How it works

### rsync command

The rsync command run is the following:

```sh
rsync \
    -a `# archive mode` \
    -v `# increase verbosity` \
    -R `# use relative path names` \
    {{local-path}}/./{{group}}/{{name}}/{{version}} `# rsync source` \
    {{remote-user}}@{{remote-host}}:{{remote-path}} `# rsync destination`
```

Where the _{{}}_ parameters are as follows:

**local-path**: Local output directory of Codox generated documentation.
**group, name, version**: Project group/name/version (Maven artifactId,groupId,version) used fo output directory structure.
**remote-user, remote-host, remote-path**: To be configured by user of plugin

The reason I've created this as a lein plugin instead of writing a
script is because lein conveniently provides access to the
group/name/version of the project. And aquiring the context of the
local output path of the generated documentation can be achieved by
reusing the same code that codox itself uses to determine where the
documentation files will be output:

```clojure
(or (:output-path (:codox project))                        ;; Use Codox :output-path
    (str (io/file (:target-path project "target") "doc"))) ;; Or default to project target/doc directory
```

### Relative output directory

The `-R` flag to rsync combined with the `/./` separator in the source
limits the amount of path information that is used for the remote. So
that the remote output directory structure is as follows:

```
{{remote-path}}/{{group}}/{{name}}/{{version}}
```

## License

Copyright © 2016 Oliver Holworthy

Distributed under the Eclipse Public License either version 1.0 or any later version.
