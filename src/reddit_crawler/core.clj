(ns reddit-crawler.core
  (:require [reddit.clj.core :as reddit])
  (:use [clojure.pprint :only [pprint]]
        [reddit-crawler.util]))

(defn login
  "Make sure to return a valid rc"
  [creds]
  (or (:rc creds) (reddit/login (:user creds) (:pass creds))))

(defn comment-authors
  "Get the authors of a reddit, a list of comments, a comment etc."
  [comments]
  (get-items comments :author))

(defn author-posts
  "Get post ids from author's posts"
  [author-posts]
  (get-items author-posts :id))

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

(defn neighbour-posts
  "Get neighboring posts. First we get all of the first author then
  all the second etc. This function should be able to use hooks."
  [post & {:keys [rc user pass order]
           :or {order 1}
           :as creds}]

  (let [rc (login creds)
        posts (if (coll? post) post [post])
        authors (lazy-mapcat #(neighbour-post-authors-m % :rc rc) posts)
        neighbours (lazy-mapcat #(neighbour-author-posts-m % :rc rc) authors)]
    (if (> order 1)
      (lazy-mapcat #(neighbour-posts % :order (dec order) :rc rc) neighbours)
      neighbours)))

;; XXX: This is basically an example. Retrievers should be implemented
;; with packages that are also modular.
;;
;; For example here we have a perfectly capable comment-retriever but it can be much smarter when rating or
(defn comment-retriever
  "Retriever using the post comments' authors."
  [post rating & rc]
  (let [authors (neighbour-post-authors-m posts :rc rc)
        grouped-posts (map (fn [[i a]]  ;XXX: use upvotes for rating
                             [(/ 1 i) (neighbour-author-posts-m a :rc rc)])
                           (lazy-enumerate authors))]
    (lazy-mapcat                        ;Ungroup and re-rate posts
     (fn [[author-rating authors-posts]]
       (map (fn [[i p]]
              [(* author-rating (/ 1 ip)) p])
            (lazy-enumerate authors-posts)))
     grouped-posts)))

(def post-retrievers
  "Functions that receve a post and it's rating and optionally a login
  cookie. return a lazy seq of conses of related (POST . RATING)."
  nil)

(defn unique-posts
  "From a list posts get a dict that show how many of each there was
  in a set."
  [posts]
  (reduce #(assoc %1 %2 (inc (%1 %2 0))) {} posts))

(defn post-prop
  "Get a post property given the id. Just so you konw :selftext is the
  body and :title is the title"
  [post-id prop & {:keys [rc user pass
                          order]
                   :or {order 1}
                   :as creds}]
  (first
   (get-items (reddit/comments (login creds) post-id) prop)))

(defn -main
  "Main function."
  [& args]
  (println "Hello world from reddit"))
