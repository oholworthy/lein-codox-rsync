(ns leiningen.codox-rsync
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [leiningen.codox :refer [codox]]))

(defn codox-rsync
  "Build codox documentation and release to remote host with rsync.

  Configuration:

  {:codox {:rsync {:remote-path \"\"
                   :remote-user \"\" ;; Optional (Default: Current user)
                   :remote-host \"\" ;; Optional (Default: Local machine)
                   }}}

  Destination directory:

  {{remote-path}}/{{group}}/{{name}}/{{version}}

  where group, name, version are properties of the project in project.clj"
  [project]
  (let [version-path (str (if-let [group (:group project)] (str group "/") "")
                          (:name project) "/" (:version project))
        output-path (or (:output-path (:codox project))
                        (str (io/file (:target-path project "target") "doc")))]
    (println "Running Codox")
    (codox (assoc-in project [:codox :output-path] (str output-path "/" version-path)))
    (let [{:keys [remote-path remote-user remote-host]} (:rsync (:codox project))
          remote-path (cond->> remote-path remote-host (str remote-host ":") remote-user (str remote-user "@"))
          local-path (str output-path "/./" version-path)
          sh-args ["rsync" "-avR" local-path remote-path]]
      (if-not remote-path
        (println "no [:codox :rsync :remote-path] specified in project.clj")
        (do (println "Syncing" output-path "to remote" remote-path)
            (println (str/join " " sh-args))
            (println (:out (apply sh sh-args))))))))
