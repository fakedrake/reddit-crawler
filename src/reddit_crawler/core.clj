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

(defn login
  "Make sure to return a valid rc"
  [creds]
  (or (:rc creds) (reddit/login (:user creds) (:pass creds))))

(defn neighbour-post-authors
  "Get psot neighboring authors. Login or login anonymously"
  [post-id & {:keys [rc user pass] :as creds}]
     (comment-authors (reddit/comments (login creds) post-id)))

(defn neighbour-author-posts
  "Get psot neighboring authors. Login or login anonymously"
  ([author & {:keys [rc user pass] :as creds}]
     (author-posts (reddit/user-submitted (login creds)  author))))

(defn neighbour-posts
  "Get neighboring posts. First we get all of the first author then
  all the second etc."
  [post & {:keys [rc user pass] :as creds}]
  (let [rc (login creds)
        ;; Note that to get the authors we make only one call to
        ;; reddit so dont break your head over making this lazy
        authors (neighbour-post-authors post :rc rc)]
    (mapcat #(neighbour-author-posts % :rc rc) authors)))

(defn -main
  "Main function."
  [& args]
  (println "Hello world from reddit"))
