(ns dhex.util
  (:require [clojure.string :as string :refer [join split]]
            [dhex.subs :as subs :refer [subscribe]]
            [re-frame.core :as rf :refer [dispatch]]
            [clojure.string :as str]))

(defn make-class
  [& args]
  (join " " args))

(defn get-date
  [date]
  (js/Date. date))

(defn get-year
  [date]
  (.getFullYear (get-date date)))

(defn get-date-string
  [date]
  (.toDateString (get-date date)))

(defn now
  []
  (js/Date.))

(defn current-year
  []
  (-> (now)
      (.getFullYear)))

(defn split-at-dot
  [string]
  (-> string
      (split #"\.")
      first
      (str ".")
      (string/capitalize)))

(defn tag-light
  [tag]
  (let [active-tag (subscribe :tag)]
    [:p.tag.tag-light {:class (str (when (= active-tag tag) "active"))
                       :on-click #(dispatch [:get-articles {:tag tag
                                                            :limit 10
                                                            :offset 0}])} tag]))

(defn tag-dark
  [tag]
  (let [active-tag (subscribe :tag)]
    [:p.tag.tag-dark  {:class (str (when (= active-tag tag) "active"))
                       :on-click #(dispatch [:get-articles {:tag tag
                                                            :limit 10
                                                            :offset 0}])} tag]))

(defn alternative-view
  ([]
   (alternative-view nil))
  ([kw]
   [:div
    [:p "Nothing to show here!"]
    (cond
      (= kw :articles) [:p.cursor-pointer.link {:on-click #(dispatch [:navigate :editor :slug "new"])} "Create a new article?"]
      (= kw :tags) [:p.cursor-pointer  "Create a new tag?"]
      :else [:p.cursor.mt-5.text-base "Dont forget to smile."]) ;; this will most likely never be used
    ]))

(defn display-error
  [error]
  [:div.app [:p.error (str "An error occured: " error)]])

(defn input-component
  [{:keys [id type placeholder on-change value label] :or {type "text"}}]
  [:div.app
   (when label [:p.text-gray-700.mb-1 {:class (str "text-[14px]")} (string/capitalize label)])
   [:input.w-full.px-1.py-4 {:id id
                             :type type
                             :placeholder (string/capitalize placeholder)
                             :on-change on-change
                             :value value}]])
(defn color-variants
  [variant]
  (let [default "bg-gray-600 text-gray-100 hover:text-gray-300 "]

    (case variant
      "default" default
      "danger" "bg-red-600"
      default)))

(defn size-variants
  [size]

  (let [default " py-2 w-full rounded-lg text-lg"]
    (case size
      "default"  default
      "small" "h-full px-3 rounded-md"
      default)))

(defn button-component
  [{:keys [label size variant disabled?] :or {variant "default" size "default"}}]
  [:button {:class (string/join "." [(color-variants variant) (size-variants size)])
            :disabled disabled?}
   (string/capitalize label)])
