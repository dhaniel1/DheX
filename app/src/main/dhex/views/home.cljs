(ns dhex.views.home
  (:require
   [re-frame.core :as rf :refer [dispatch]]
   [dhex.routes :as routes]
   [dhex.util :as u :refer [tag-dark tag-light alternative-view]]
   [dhex.subs :as subs :refer [subscribe]]
   [dhex.views.shared.pagination :as pagination :refer [pagination]]))

(defn hero
  []
  [:section
   [:div.app-home-hero.flex.flex-col.items-center.justify-center.w-full.mb-6.p-4.md:p-10
    [:p.app-text-logo.text-7xl "DheX"]
    [:p "Big things start small"]]])

(defn feed-preview
  [{:keys [title description slug author tagList createdAt updatedAt]
    :or {slug ""
         author {:username ""}}}]

  [:div.app-home-main-content-feeds-all-feeds-feed-preview

   ;;feed preview title section
   [:div.app-home-main-content-feeds-all-feeds-feed-preview-title.flex.items-center.gap-2
    [:img.app-home-main-content-feeds-all-feeds-feed-preview-title-author-avatar.cursor-pointer
     {:src (:image author)
      :alt "profile avatar"
      :on-click (fn [event] (.preventDefault event)
                  (dispatch [:navigate :profile :user-id (:username author)]))}]

    [:div
     [:p.app-home-main-content-feeds-all-feeds-feed-preview-title-username.cursor-pointer
      {:on-click (fn [event] (.preventDefault event)
                   (dispatch [:navigate :profile :user-id (:username author)]))}
      (:username author)]

     [:p.app-home-main-content-feeds-all-feeds-feed-preview-title-date
      (->  (if (seq updatedAt) createdAt updatedAt)
           u/get-date-string)]]]

   ;; feed preview Body Section
   [:div.app-home-main-content-feeds-all-feeds-feed-preview-body.flex.flex-col.cursor-pointer
    {:on-click (fn [event] (.preventDefault event)
                 (dispatch [:navigate :article :slug slug]))}
    [:h2.app-home-main-content-feeds-all-feeds-feed-preview-body-title (u/split-at-dot title)]
    [:div.app-home-main-content-feeds-all-feeds-feed-preview-body-description description]]

;;feed preview Footer section
   [:div.app-home-main-content-feeds-all-feeds-feed-preview-footer.flex.justify-between.mb-4
    [:p.cursor-pointer {:on-click (fn [event] (.preventDefault event)
                                    (dispatch [:navigate :article :slug slug]))}
     "Read More...."]
    [:div.app-home-main-content-feeds-all-feeds-feed-preview-footer-tags.flex
     [:div.flex.gap-1
      (for [tag-item tagList]
        ^{:key tag-item} [tag-light tag-item])]]]])

(defn all-feeds
  [feeds]
  [:div.app-home-main-content-feeds-all-feeds
   (if (seq feeds)
     (for [feed feeds]
       ^{:key (:slug feed)} [feed-preview feed])
     [alternative-view :articles])])

(defn tags-comp
  [{:keys [tags]}]

  [:div.flex.flex-wrap.gap-1.items-center
   (for [tag tags]
     ^{:key tag} [tag-dark tag])])

(defn- main
  []
  (let [user (subscribe :user)
        loading-articles? (subscribe :loading-articles?)
        articles (subscribe :articles)
        loading-tags?  (subscribe :loading-tags?)
        all-tags (subscribe :tags)
        filter (subscribe :filter)
        articles-count (subscribe :articles-count)
        feed-articles-error (subscribe :get-feed-articles-error)
        get-tags-error (subscribe :get-tags-error)]

    [:section
     [:div.app-home-main.mx-auto {:class (str "w-11/12")}
      [:div.app-home-main-content.flex.gap-2.mb-8
       [:div.flex.flex-col.w-full {:class (str "md:w-4/5")}
        [:div.app-home-main-content-feeds-selector.flex.gap-1
         (when (seq user)
           [:button.p-3 {:class (when (:feed filter) "active")
                         :on-click #(dispatch [:get-feed-articles {:limit 10 :offset 0}])} "Your Feed"])

         [:button.p-3 {:class (when-not (or (:tag filter) (:feed filter)) "active")
                       :on-click #(dispatch [:get-articles {:limit 10 :offset 0}])} "Global Feed"]
         (when (:tag filter)  [:button.p-3 {:class "active"} (str "#" (:tag filter))])]
        [:div.flex
         (if loading-articles?
           [:div
            [:p "Loading Articles..........."]]
           (if  feed-articles-error
             (u/display-error feed-articles-error)
             [all-feeds articles]))]]

       [:div.app-home-main-content-tags.hidden.md:flex
        [:div.app-home-main-content-tags-content.p-3.h-fit
         [:p.text-center.pb-2 "Popular Tags"]
         (if loading-tags?
           [:p "Loading Tags......"]
           (if  feed-articles-error
             (u/display-error feed-articles-error)
             [tags-comp all-tags]))]]]

      (when-not (or loading-articles? (< articles-count 10))
        [pagination])]]))

(defn home-view []
  [:div.app-home
   [hero]
   [main]])

(defmethod routes/panels :home-view [] [home-view])
