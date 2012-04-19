(ns pt-stories.core
  (:use hiccup.core
        hiccup.page)
  (:require [clj-http.client :as client]))

(def api-base "http://www.pivotaltracker.com/services/v3")

(def data 
  [])


(defn build-xml-struct
  "returns the xml for posting"
  [{:keys [type points description name labels]}]
  (html
    [:story
      [:story_type (h type)]
      [:estimate (h points)]
      [:description (h description)]
      [:name (h name)]
      [:labels (h(clojure.string/join ", " labels))]
      ]))

(defn add-story
  "adds a single story to your project
   example:
   (story {:name \"test\" :labels [\"foo\"] :points 1 :description \"foo bar baz\" :type \"feature\"})
   (add-story token project-id story)"
  [token project-id story]
  (let [url (clojure.string/join "/" [api-base "projects" project-id "stories"])
        body (build-xml-struct story)]
    (client/post
      url
      {:headers {"X-TrackerToken" token "Content-type" "application/xml"}
       :body body})))
  
(defn add-stories
  "the method you can use to add all the stories in data: eg,
   (add-stories {:token token :project-id project-id :stories data})"
  [{:keys [token project-id stories]}]
  (doseq 
    [story stories] 
    (add-story token project-id story)))