(ns reddit-crawler.core
  (:require [reddit.clj.core :as reddit])
  (:use [clojure.pprint :only [pprint]]))


(defn flatten-keys*
  "Search in a tree appending each key in keys to create a vector of
  keys. Then present it all in map of key-lists->leaves"
  [return-map keys tree]

  (if tree
    (cond
     (vector? tree) (apply merge
                           (map
                            (fn [v index]
                              (flatten-keys* return-map (conj keys index) v))
                            tree (range (count tree))))

     (map? tree) (apply merge
                        (map
                         (fn [[k v]]
                           (flatten-keys* return-map (conj keys k) v))
                         (seq tree)))

     :else (assoc return-map keys tree))))

(defn flatten-keys
  "Find the path of keys for each leaf value."
  [m] (flatten-keys* {} [] (vec m)))


(defn get-items
  "Get items from a tree based on their immediate parent."
  [tree key]
  (apply sorted-set
   (remove nil?
           (map
            (fn [[k v]]
              (if (= (last k) key) v))
            (seq (flatten-keys tree))))))


(defn comment-authors
  "Get the authors of a reddit, a list of comments, a comment etc."
  [comments]
  (get-items comments :author))

(defn author-posts
  "Get post ids from author's posts"
  [author-posts]
  (get-items author-posts :id))

(defn -main
  "Main function."
  [& args]
  (println "Hello world from reddit"))
