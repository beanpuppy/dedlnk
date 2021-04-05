(ns dedlnk.core
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [clj-http.client :as client]
            [cheshire.core :as json]))

(def h {"User-Agent" "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 11.2; rv:86.0) Gecko/20100101 Firefox/86.0"})
(def link-regex #"https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")
(def wayback-url "http://archive.org/wayback/available?url=")

(defn is-markdown? [x]
  (str/ends-with? x ".md"))

(defn dir->files [directory]
  (filter is-markdown? (map (fn [f]
                              (str directory "/" f)) (.list (io/file directory)))))

(defn link->status [link]
  (try
    (get (client/get link {:throw-exceptions false :headers h :cookie-policy :none}) :status)
    (catch Exception e "ERRCONNECT")))

(defn link->wayback [link]
  (let [res (clj-http.client/get (str wayback-url link))]
    (json/parse-string (:body res) true)))

(defn link->closest [link]
  (-> (link->wayback link) :archived_snapshots :closest :url))

(defn file->links [file]
  (re-seq link-regex (slurp file)))

(defn dir->links [directory]
  (for [x (dir->files directory)
        l (file->links x)]
    {:file x :link (get l 0)}))

(defn find-dedlnks [directory]
  (doseq [l (dir->links directory)]
    (let [file (:file l)
          link (:link l)
          status (link->status link)]
      (when (not= status 200)
        (println "----------")
        (println "File:" file)
        (println "Link" link "(" status ")")
        (println "Archive:" (link->closest link))))))

(def cli-options
  [["-d" "--dir" "Directory of files."]
   ["-h" "--help"]])

(defn -main [& args]
  (let [opts (parse-opts args cli-options)]
    (if (-> opts :options :dir)
      (find-dedlnks (get (:arguments opts) 0))
      (println (:summary opts)))))
