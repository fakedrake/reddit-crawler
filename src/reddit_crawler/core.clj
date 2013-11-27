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

(def neighbour-post-authors-m (memoize neighbour-post-authors))

(defn neighbour-author-posts
  "Get psot neighboring authors. Login or login anonymously"
  [author & {:keys [rc user pass] :as creds}]
  (when (not= author "[deleted]")
    (author-posts (reddit/user-submitted (login creds) author))))

(def neighbour-author-posts-m (memoize neighbour-author-posts))

(defn lazy-mapcat
  [f coll]
  (lazy-seq
   (if (not-empty coll)
     (concat
      (f (first coll))
      (lazy-mapcat f (rest coll))))))

(defn neighbour-posts
  "Get neighboring posts. First we get all of the first author then
  all the second etc."
  [post & {:keys [rc user pass order]
           :or {order 1}
           :as creds}]
  (let [rc (login creds)
        posts (if (coll? post) post [post])
        authors (lazy-mapcat #(neighbour-post-authors-m % :rc rc) posts)
        neighbours (lazy-mapcat #(neighbour-author-posts-m % :rc rc) authors)]
    (println "order: " order ", size:" (count neighbours))
    (if (> order 1)
      (lazy-mapcat #(neighbour-posts % :order (dec order) :rc rc) neighbours)
      neighbours)))

(defn unique-posts
  "From a list posts get a dict that show how many of each there was
  in a set."
  [posts]
  (reduce #(assoc %1 %2 (inc (%1 %2 0))) {} posts))

(defn post-prop
  "Get a post property given the id. Just so you konw :selftext is the
  body and :title is the title" [post-id prop & {:keys [rc user pass
  order]
                   :or {order 1}
                   :as creds}]
  (first
   (get-items (reddit/comments (login creds) post-id) prop)))

(defn -main
  "Main function."
  [& args]
  (println "Hello world from reddit"))
