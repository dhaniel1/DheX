(ns dhex.views.shared.navbar
  (:require [re-frame.core :as rf :refer [dispatch]]
            [dhex.subs :as subs :refer [subscribe]]
            [dhex.routes :as routes :refer [url-for]]))

(defn nav
  []
  (let [active-page (subscribe :active-page)
        user (subscribe :user)]

    [:<>
     [:p.link {:class (when (= active-page :home) "active")
               :on-click #(dispatch [:navigate :home])} "Home"]

     (if (empty? user)

       [:<>
        [:p.link {:class (when (= active-page :login) "active")
                  :on-click #(dispatch [:navigate :login])} "Sign In"]
        [:p.link {:class (when (= active-page :register) "active")
                  :on-click #(dispatch [:navigate :register])} "Sign Up"]]

       [:<>
        [:p.link {:class (when (= active-page :editor) "active")
                  :on-click #(dispatch [:navigate :editor :slug "new"])} "New Article"]
        [:p.link {:class (when (= active-page :settings) "active")
                  :on-click #(dispatch [:navigate :settings])} "Settings"]
        [:a.link {:class (when (= active-page :profile) "active")
                  :on-click #(dispatch [:navigate :profile :user-id (-> user :username)])} "Profile"]
        [:button.app-button.small.transparent.danger-border {:on-click #(dispatch [:logout])} "Logout"]])]))

(defn mobile-nav
  []
  (let [active-page (subscribe :active-page)
        user (subscribe :user)
        toggle-menu-modal?  (subscribe :toggle-menu-modal?)]

    [:<>
     [:div.logo-icon.hamburger {:on-click #(dispatch [:toggle-menu-modal])}

      (when toggle-menu-modal?
        [:div.absolute.top-12.right-5.bg-gray-200.rounded-md.hover:bg-gray-300
         [:ul.flex.flex-col
          [:p.px-6.py-3 {:class (when (= active-page :home) "active")
                         :on-click #(dispatch [:navigate :home])} "Home"]
          (if (empty user)

            [:<>
             [:p.px-6.py-3 {:class (when (= active-page :login) "active")
                            :on-click #(dispatch [:navigate :login])} "Sign In"]
             [:p.px-6.py-3 {:class (when (= active-page :register) "active")
                            :on-click #(dispatch [:navigate :register])} "Sign Up"]]

            [:<> [:li
                  [:p.px-6.py-3 {:class (when (= active-page :editor) "active")
                                 :on-click #(dispatch [:navigate :editor :slug "new"])} "New Article"]]
             [:li
              [:p.px-6.py-3 {:class (when (= active-page :settings) "active")
                             :on-click #(dispatch [:navigate :settings])} "Settings"]]
             [:li
              [:p.px-6.py-3 {:class (when (= active-page :profile) "active")
                             :on-click #(dispatch [:navigate :profile :user-id (-> user :username)])} "Profile"]]
             [:li
              [:p.px-6.py-3 {:on-click #(dispatch [:logout])} "Logout"]]])]])

;; When not empty
      ]]))

(defn navbar
  []
  (let [active-page (subscribe :active-page)
        user (subscribe :user)]
    [:section.navbar-section.app
     [:nav.app-navbar.flex.justify-between.items-center.px-8.py-4.mx-auto
      [:p.app-text-logo.text-3xl.cursor-pointer {:on-click #(dispatch [:navigate :home])
                                                 :class (str "md:text-[2.5rem]")} "DheX"]

      [:div.app-navbar-navlinks.flex.gap-3.text-lg.flex.md:hidden
       [mobile-nav]]

      [:div.app-navbar-navlinks.flex.gap-3.text-lg.hidden.md:flex
       [nav]]]]))

