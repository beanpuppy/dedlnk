(defproject dedlnk "0.1.0-SNAPSHOT"
  :description "Tool to find dead links"
  :url "https://github.com/beanpuppy/dedlnk"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.206"]
                 [clj-http "3.12.0"]
                 [cheshire "5.10.0"]]
  :repl-options {:init-ns dedlnk.core}
  :main dedlnk.core)
