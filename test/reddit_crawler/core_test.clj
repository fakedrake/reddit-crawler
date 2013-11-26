(ns reddit_crawler.core-test
  (:require [clojure.test :refer :all]
            [clojure.set :refer :all]
            [reddit-crawler.core :refer :all]
            [reddit.clj.core :as reddit]))

(def rc (reddit/login "clojurepr" "12345"))
(def test-id "1r9ze0")
(def test-id2 "1r58si")
(def test-pic "1rgowj")
(def test-pic-neighbors ["1q6pbz" "1qn3eo" "1rgowj" "1f5gi6" "1f5q0s" "1fc7k2" "1fpnn5" "1hbuzh" "1hmoto" "1hn0r8" "1hn3ax" "1ocr41"])

(deftest comment-authors-test
  (testing "Comment tree authors."
    (is (subset? #{"Filon" "he" "me" "you"}
                 (comment-authors
                  [{:data {:author "Filon"}}
                   {:data {:author "me"}}
                   {:author "you"
                    :replies
                    [{:author "he"}]}])))))

(deftest comment-authors-field-test
  (testing "Actually download a thread."
    (is (subset? #{"WEED_WIZARD" "captionday" "clojurepr" "pornitoueleus"}
                 (comment-authors (reddit/comments rc test-id)))))

  (testing "Actually download a thread with the nice interface."
    (is (subset? #{"WEED_WIZARD" "captionday" "clojurepr" "pornitoueleus"}
                 (neighbour-post-authors test-id))))

  (testing "Actually download a thread."
    (is (subset? #{"WEED_WIZARD" "captionday" "clojurepr" "pornitoueleus"}
                 (neighbour-post-authors test-id :user "clojurepr" :pass "12345")))))

(deftest authors-posts-test
  (testing "From the posts of an author."
    (is (subset? #{"1qmeuo"}
                 (author-posts (reddit/user-submitted rc "clojurepr"))))))

(deftest neighbouring-posts-test
  (testing "Neighbouring posts."
    (is (subset? (set test-pic-neighbors) (set (neighbour-posts test-pic :rc rc))))))
