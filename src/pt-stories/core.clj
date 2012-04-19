(ns pt-stories.core
  (:use hiccup.core
        hiccup.page)
  (:require [clj-http.client :as client]))

(def api-base "http://www.pivotaltracker.com/services/v3")

(def data 
  ; a list of service
  [
    ; example service
    {:name "foo" 
     :type "feature" 
     :points 1
     :labels ["bar"]
     :description "baz"
    }
  ])

(defn build-xml-struct
  "returns the xml for posting"
  [{:keys [type points description name labels]}]
  (html
    [:story
      [:story_type type]
      [:estimate points]
      [:description description]
      [:name name]
      [:labels (clojure.string/join "," labels)]
      ]))

(defn add-story
  "adds a single story to your project"
  [token project-id story]
  (let [url (clojure.string/join "/" [api-base "projects" project-id "stories"])
        body (build-xml-struct story)]
    (client/post
      url
      {:headers {"X-TrackerToken" token "Content-type" "application/xml"}
       :body body})))
  
(defn add-stories
  "the method you can use to add all the stories in data: eg, (add-stories token project-id data)"
  [token project-id stories]
  (doseq 
    [story stories] 
    (add-story token project-id story)))