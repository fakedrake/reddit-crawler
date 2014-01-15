(ns reddit-crawler.comment-retriever
  (:require [reddit.clj.core :as reddit])
  (:use [clojure.pprint :only [pprint]]
        [reddit-crawler.util]))

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
