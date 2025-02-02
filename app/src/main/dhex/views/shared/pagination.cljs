(ns dhex.views.shared.pagination
  (:require [dhex.subs :as subs :refer [subscribe]]
            [re-frame.core :as rf :refer [dispatch]]))

(defn pagination []
  (let [articles-count (subscribe [:articles-count])
        tag (subscribe [:tag])
        offset (subscribe [:offset])
        pages (range 0 articles-count 10)]

   
    [:section.mx-auto.w-ful.mb-6
     [:div.pagination.flex.flex-wrap.gap-1
      (for [page-inst (range 1 (inc (count pages)))]
        (let [offset-param (* 10 (dec page-inst))]

          ^{:key page-inst} [:div.pagination-page
                             {:class (when (= offset offset-param) "active")
                              :on-click #(if tag
                                           (dispatch [:get-articles {:tag tag
                                                                     :offset offset-param
                                                                     :limit 10}])
                                           (dispatch [:get-articles {:offset offset-param
                                                                     :limit 10}]))} [:p page-inst]] 
         ))]]))
