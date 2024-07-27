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
      (let [loading-register-user? (subscribe :loading-register-user?)
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

            (u/password-compnent {:password-visible (:password-visible @cred)
                                  :on-change  #(onChange % :password)
                                  :value (:password @cred)
                                  :on-click #(onClick :password-visible)})


            (u/button-component {:disabled?  loading-register-user?
                                 :label (if loading-register-user? "Signing up..." "Sign up")})]]

          [:p.text-center "Already have an account? "
           [:span.text-blue-600.font-semibold.cursor-pointer {:on-click #(dispatch [:navigate :login])} "Sign In"]]]
;; Form Input components
         ]))))
(defmethod routes/panels :register-view [] [register-page])
