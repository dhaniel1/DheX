(ns dhex.views.login
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [dispatch]]
            [dhex.routes :as routes]
            [dhex.subs :as sub :refer [subscribe]]
            [dhex.util :as u]))

(defn login-page []
  (let [cred (r/atom {:email "" :password "" :password-visible false})]

    (fn []
      (let [loading-login-user? (subscribe :loading-login-user?)
            register (fn [event cred]
                       (.preventDefault event)
                       (dispatch [:login-user cred]))
            onChange (fn [event key] (swap! cred assoc key (-> event .-target .-value)))
            onClick (fn [key] (swap! cred update key #(not %)))]

        [:div.w-full.px-14.mx-auto {:class (str "min-w-[400px] max-w-[500px] ")}

         ;; Title Component
         [:section
          [:div.flex.flex-col.align-start.mx-auto.mt-8
           [:h3.font-semibold {:class "text-[2.5rem]"} "Sign in"]
           [:p.text-base.text-start.text-gray-600 "Welcome back! Please enter your details."]]]

         ;; Form components
         [:section
          [:div.flex.flex-col.mx-auto.mb-6.mt-8
           [:form.flex.flex-col.gap-6.w-full {:on-submit #(register % (dissoc @cred :password-visible))}
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
                                  :value (:password @cred)
                                  })

             [:div.is-visible {:on-click #(onClick :password-visible)
                               :class (if (:password-visible @cred) "yes-visible"  "not-visible")}]]

            (u/button-component {:disabled? loading-login-user?
                                 :label (if loading-login-user? "Signing in..." "Sign in")})]]

          [:p.text-center "Don't have an account? "
           [:span.text-blue-600.font-semibold.cursor-pointer {:on-click #(dispatch [:navigate :register])} "Sign Up"]]]]))))

;; Form Input components
(defmethod routes/panels :login-view [] [login-page])
