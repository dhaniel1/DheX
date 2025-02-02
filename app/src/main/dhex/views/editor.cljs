(ns dhex.views.editor
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch]]
            [clojure.string :as string :refer [join trim split]]
            [dhex.subs :as subs :refer [subscribe]]
            [dhex.routes :as routes]
            [dhex.util :as u]))

(defn editor-page
  []
  (let [{:keys [title description body tagList slug] :as active-article} (subscribe [:active-article])
        tagList (join " " tagList)
        default {:title title :description description :body body :tagList tagList}
        content (r/atom default)]

    (fn []
      (let [loading-article? (subscribe :loading-article?)
            onChange (fn [event key] (swap! content assoc key (-> event .-target .-value)))
            onSubmit (fn [event content slug]
                       (.preventDefault event)
                       (dispatch [:upsert-article {:slug    slug
                                                   :article {:title       (trim (or (:title content) ""))
                                                             :description (trim (or (:description content) ""))
                                                             :body        (trim (or (:body content) ""))
                                                             :tagList     (split (:tagList content) #" ")}}]))]

        [:div.w-full.px-14.mx-auto {:class (str "min-w-[400px] max-w-[700px] ")}

   ;; Title Component
         [:section
          [:div.flex.flex-col.justify-center.mt-3.mb-6.mx-auto
           [:h3.font-semibold {:class "text-[2.5rem]"}
            (str (if false "Update" "Create new") " article") ""]
           [:p.text-base.text-start.text-gray-600 (str "Fill in the fields to " (if false "update" "publish a new") "  article")]]] ;; Fix static conditional

;; Form components
         [:section.app
          [:div.flex.flex-col.mx-auto
           [:form.app-register-body-form.flex.flex-col.gap-6 {:on-submit #(onSubmit % @content slug)}

            (u/input-component {:id "title"
                                :type "text"
                                :placeholder "Article title"
                                :on-change #(onChange % :title)
                                ;; :default-value (:image @cred)
                                :value (:title @content)})

            (u/input-component {:id "about"
                                :type "text"
                                :placeholder "About Article"
                                :on-change #(onChange % :description)
                                ;; :default-value (:article @cred)
                                :value (:description @content)})

            [:textarea.w-full {:id "article"
                               :type "text"
                               :rows 10
                               :placeholder "Write your article in markdown"
                                                         ;; :default-value (:article @cred)
                               :on-change #(onChange % :body)
                               :value (:body @content)}]

            #_(u/input-component {:id "article"
                                  :type "text"
                                  :rows 10
                                  :placeholder "Write your article in markdown"
                                ;; :default-value (:article @cred)
                                  :on-change #(onChange % :body)
                                  :value (:body @content)})

            (u/input-component  {:id "tags"
                                 :type "text"
                                 :placeholder "Your tags"
                                 :on-change #(onChange % :tagList)
                                 ;; :default-value (:tags @cred)
                                 :value (:tagList @content)})

            (u/button-component {:disabled loading-article?
                                 :label (if active-article
                                          (if loading-article? "Updating...."  "Update Article")
                                          (if loading-article? "Publishing...."  "Publish Article"))})]]]]))))

(defmethod routes/panels :editor-view [] [editor-page])

