(ns pt-stories.core
  (:use hiccup.core
        hiccup.page)
  (:require [clj-http.client :as client]))

(def api-base "http://www.pivotaltracker.com/services/v3")

(def data 
  [
    ; Dynamic Service Creation
    {:name "Add Unlisted Service from Service Search" 
     :type "feature" :points 1 :labels ["sl"]
     :description "When a search returns no results the user will have the option to dynamically create the service from the search results input."}

    {:name "Unlisted Service inherits its service type from its ancestors organizations"
     :type "feature" :points 3 :labels ["sl", "shared"]
     :description "Service needs to default to one time fee or visit based and should get this from 'walking the tree'"}

    {:name "View Unlisted Service from The Service Request Setup View"
     :type "feature" :points 2 :labels ["sl"]
     :description "A unlisted service should be shown on the list of services on the Service Request Setup View"}

    {:name "View Unlisted Service from the Visit Calendar View"
     :type "feature" :points 2 :labels ["sl"]
     :description "An unlisted service should be shown on the visit calendar as a pending service without the ability to edit"}

    {:name "View Unlisted Service from the Review View"
     :type "feature" :points 2 :labels ["sl"]
     :description "An unlisted service should show on the review view as a pending service"}

    {:name "View Unlisted Service on the Excel Spreadsheet Export"
     :type "feature" :points 2 :labels ["sl"]
     :description "The excel spreadsheet needs to show the pending service"
     }

    {:name "Notify appropriate catalog manager when 'other' services are added"
     :type "feature" :points 3 :labels ["sl"]
     :description "SL needs to report which Service Request holds the service for lookup by catalog manager"}

    ; Subsidy Changes
    {:name "Subsidy Page needs to dynamically adjust to a change in pricing schedule"
     :type "feature" :points 3 :labels ["sl"]
     :description "If data stored is invalid then it wont even load the screen because of validation on reset"}
    
    {:name "Admin overrides to subsidy amount"
     :type "feature" :points 3 :labels ["sl", "obis-shared"]
     :description "subsidy screen in SL may not view the data on GET as valid, also should allow to continue with overage amount on save & cont. need to research options, obis-shared should be ok but need to check"}
   
    ; Pricing Structure Change
    ; requires more thought
    {:name "add Service#pricing_map function to the Service model"
     :type "feature" :points 2 :labels ["sl"]
     :description "pricing map will handle the logic of returning the appropriate map to compute pricing"}

    {:name "Pricing calculations need to handle pricing maps with values set to zero or null"
     :type "chore" :points 2 :labels ["sl" "obis-shared"]
     :description "they probably already do, but we should check; associated with dynamic service creation"}
    
    {:name "Refactor app to handle new pricing map function"
     :type "chore" :points 3 :labels ["sl"]
     :description "there will be extensive refactoring of tests and src code that use the pricing map directly as an attribute"}

    {:name "Refactor obis-shared where it would refer to the pricing map directly to a function instead"
     :type "chore" :points 2 :labels ["obis-shared"]
     :description "update obis-shared to use new data structure"}

    ; Store Historic Data
    {:name "Obis-Common should take an optional parameter to update/create functions for meta information"
     :type "chore" :points 5 :labels ["obis-common"]
     :description "add the ability for the diff to include a meta key that can contain any meta info about the change (user_id, app)"}

    {:name "Any routes (post/put) we care about posting meta info need to be changed and tested"
     :type "chore" :points 8 :labels ["obis-shared" "sl" "up" "ap"]
     :description "this is probably best done as a before filter; injecting params en route to obis-common"}
    ])


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