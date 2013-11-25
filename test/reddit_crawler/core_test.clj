(ns reddit-crawler.core-test
  (:require [clojure.test :refer :all]
            [clojure.set :refer :all]
            [reddit-crawler.core :refer :all]
            [reddit.clj.core :as reddit]))

(def rc (reddit/login "clojurepr" "12345"))
(def test-id "1r9ze0")
(def test-id2 "1r58si")
(def real-post [[{:kind "t3",
                  :data
                  {:author "captionday",
                   :name "t3_1r9ze0",
                   }}]
                [{:kind "t1",
                  :data
                  {:author "WEED_WIZARD",
                   :name "t1_cdl4nsi",
                   :replies
                   {:kind "Listing",
                    :data
                    {:modhash "2hhvptgb2bb7fa3f7142e748f0e1efc4dfea43053e953faa37",
                     :children
                     [{:kind "t1",
                       :data
                       {:author "pornitoueleus",
                        :name "t1_cdl7uab",
                        :replies
                        {:kind "Listing",
                         :data
                         {:modhash
                          "2hhvptgb2bb7fa3f7142e748f0e1efc4dfea43053e953faa37",
                          :children
                          [{:kind "t1",
                            :data
                            {:author "pornitoueleus",
                             :name "t1_cdl7udo",
                             :replies "",
                             }}],
                          :after nil,
                          :before nil}},
                        }}
                      {:kind "t1",
                       :data
                       {:author "pornitoueleus",
                        :name "t1_cdl7ung",
                        :replies "",
                        }}],
                     :after nil,
                     :before nil}},
                   }}
                 {:kind "t1",
                  :data
                  {:author "pornitoueleus",
                   :name "t1_cdl7u29",
                   :replies "",
                   }}]])
(def simple-real-post
  {:author "WEED_WIZARD"
   :name "t1_cdl4nsi"
   :replies
   {:data
    {:modhash "2hhvptgb2bb7fa3f7142e748f0e1efc4dfea43053e953faa37"
     :children
     [{:kind "t1"
       :data
       {:author "pornitoueleus"
        :name "t1_cdl7uab"
        :replies
        {:kind "Listing"
         :data
         {:modhash
          "2hhvptgb2bb7fa3f7142e748f0e1efc4dfea43053e953faa37"
          :children
          [{:kind "t1"
            :data
            {:author "pornitoueleus"
             :name "t1_cdl7udo"
             :replies ""
             }}]
          :after nil
          :before nil}}
        }}
      {:kind "t1"
       :data
       {:author "pornitoueleus"
        :name "t1_cdl7ung"
        :replies ""
        }}]
     :after nil
     :before nil}}
   })

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
                 (comment-authors (reddit/comments rc test-id))))))

(deftest authors-posts-test
  (testing "From the posts of an author."
    (is (subset? #{"1qmeuo"}
                 (author-posts (reddit/user-submitted rc "clojurepr"))))))
