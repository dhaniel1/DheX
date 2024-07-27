(ns dhex.views.settings
  (:require  [reagent.core :as r]
             [re-frame.core :as rf :refer [dispatch]]
             [dhex.routes :as routes]
             [dhex.util :as u]
             [dhex.subs :as subs :refer [subscribe]]))

(defn settings-panel
  []
  (let [{:keys [bio email image username]} (subscribe :user)
        default {:bio bio
                 :email email
                 :image image
                 :username username}
        cred (r/atom default)]

    (fn []
      (let [loading-update-user? (subscribe :loading-update-user?)
            onClick (fn [key] (swap! cred update key #(not %)))
            onChange (fn [event key]  (swap! cred assoc key (-> event .-target .-value)))
            onSubmit (fn [event credentials]
                       (.preventDefault event)
                       (dispatch [:update-user credentials]))]

        [:div.w-full.px-14.mx-auto.mb-6 {:class (str "min-w-[400px] max-w-[700px] ")}

   ;; Title Component
         [:section
          [:div.flex.flex-col.justify-center.mb-6.mt-3.mx-auto
           [:h3.font-semibold {:class "text-[2.5rem]"} "Settings"]
           [:p.text-base.text-start.text-gray-600 "Update your details."]]]

;; Form components
         [:section.app
          [:div.flex.flex-col.mx-auto 
           [:form.app-register-body-form.flex.flex-col.gap-6 {:on-submit #(onSubmit % (dissoc @cred :password-visible))}

            (u/input-component {:id "avatar-url"
                                :type "text"
                                :placeholder "Url of profile picture"
                                :on-change #(onChange % :image)
                                :default-value (:image @cred)
                                :value (:image @cred)})

            (u/input-component  {:id "name"
                                 :type "text"
                                 :placeholder "Your name"
                                 :on-change #(onChange % :username)
                                 :default-value (:username @cred)
                                 :value (:username @cred)})

            [:textarea.w-full {:id "bio"
                                                            :type "text"
                                                            :rows 9
                                                            :placeholder "Short bio for you"
                                                            :default-value (:bio @cred)
                                                            :on-change #(onChange % :bio)
                                                            :value (:bio @cred)}]

            (u/input-component  {:id "email"
                                 :type "text"
                                 :placeholder "Your email"
                                 :on-change #(onChange % :email)
                                 :default-value (:email @cred)
                                 :value (:email @cred)})

            (u/password-compnent {:password-visible (:password-visible @cred)
                                  :on-change  #(onChange % :password)
                                  :value (:password @cred)
                                  :on-click #(onClick :password-visible)})

            #_[:fieldset.flex.flex.items-center
               [:input.app-register-body-form-input.w-full {:id "username"
                                                            :type (if (:password-visible @cred) "text" "password")
                                                            :placeholder "Enter your password"
                                                            :on-change  #(onChange % :password)
                                                            :default-value (:password @cred)
                                                            :value (:password @cred)}]

               [:div.is-visible {:on-click #(onClick :password-visible)
                                 :class (if (:password-visible @cred) "yes-visible" "not-visible")}]]

            (u/button-component {:disabled loading-update-user?
                                 :label (if loading-update-user? "Updating User..." "Update User")})]]]]))))

(defmethod routes/panels :settings-view [] [settings-panel])
