(ns reddit-crawler.util
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
  "Get items from a tree based on their immediate parent. Remove
  duplicates. The items are in the order they were found and in
  reverse order of depth."
  [tree key]
  (map last
       (sorted-set
        (remove nil?
                (map
                 (fn [[k v]]
                   (if (= (last k) key) [(count k) v]))
                 (seq (flatten-keys tree)))))))

(defn lazy-mapcat
  "Like mapcat but lazy. Actually like python's chain of itertools."
  [f coll]
  (if (not-empty coll)
    (concat
     (f (first coll))
     (lazy-seq (lazy-mapcat f (rest coll))))))

(defn lazy-enumerate
  "Return a lazy-seq tha starts at offset or 0."
  ([coll] (lazy-enumerate coll 0))
  ([coll offset]
     (if (not-empty coll)
       (cons
        [offset (first coll)]
        (lazy-seq (lazy-enumerate (rest coll) (inc offset)))))))
