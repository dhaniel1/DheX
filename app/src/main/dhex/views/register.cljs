(ns dhex.views.register
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch]]
            [dhex.subs :as subs :refer [subscribe]]
            [dhex.util :as u]
            [dhex.routes :as routes]))

(defn register-page
  []
  (let [cred (r/atom {:username "" :email "" :password "" :password-visible false})]

    (fn []
      (let [;; registering (subscribe :registering)
            onClick (fn [key] (swap! cred update key #(not %)))
            onChange (fn [event key]  (swap! cred assoc key (-> event .-target .-value)))
            register (fn [event credentials]
                       (.preventDefault event)
                       (dispatch [:register-user credentials]))]

        [:div.mx-auto.w-full.px-14 {:class (str "min-w-[400px] max-w-[500px] ")}

         ;; Title Component
         [:section
          [:div.mt-8.flex.flex-col.justify-start.mx-auto
           [:h3.font-semibold {:class "text-[2.5rem]"} "Sign up"]
           [:p.text-base.text-start.text-gray-600 "Please enter your details."]]]

         ;; Form components
         [:section
          [:div.flex.flex-col.mx-auto.mt-8.mb-6
           [:form.mx-auto.w-full.flex.flex-col.gap-6 {:on-submit #(register % (dissoc @cred :password-visible))}

            (u/input-component {:id "name"
                                :label "Name"
                                :type "text"
                                :placeholder "Enter your name"
                                :on-change #(onChange % :username)
                                :value (:username @cred)})

            (u/input-component {:id "email"
                                :label "Email"
                                :type "text"
                                :placeholder "Enter your email"
                                :on-change #(onChange % :email)
                                :value (:email @cred)})

            [:div.app.relative
             (u/input-component  {:id "password"
                                  :label "Password"
                                  :type (if (:password-visible @cred) "text" "password")
                                  :placeholder "Enter your password"
                                  :on-change  #(onChange % :password)
                                  :value (:password @cred)})

             [:div.is-visible {:on-click #(onClick :password-visible)
                               :class (if (:password-visible @cred) "yes-visible"  "not-visible")}]]

            (u/button-component {;;:disabled? loading-login-user?
                                 :label "Sign up" ;; (if loading-login-user? "Signing in..." "Sign in")
                                 })]]

          [:p.text-center "Already have an account? "
           [:span.text-blue-600.font-semibold.cursor-pointer {:on-click #(dispatch [:navigate :login])} "Sign In"]]]
;; Form Input components
         ]))))
(defmethod routes/panels :register-view [] [register-page])
