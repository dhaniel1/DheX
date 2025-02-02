(ns dhex.handler
  (:require  [clojure.string :as string :refer [join]]
             [compojure.core :refer [defroutes GET POST PUT OPTIONS context wrap-routes]]
             [compojure.route :as route]
             [cheshire.core :as json]
             [clj-http.client :as client]
             [clojure.pprint :as cp]
             [ring.middleware.params :refer [wrap-params]]
             [ring.middleware.cors :refer [wrap-cors]]
             [ring.middleware.reload :refer [wrap-reload]]
             [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
             [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def api-url "https://api.realworld.io/api")

(defn build-endpoint
  "concatinates api-url with / "
  [endpoint-base & args]
  (->>  args
        (cons endpoint-base)
        (join "")))

(defn wrap-api-url [handler]
  (fn [request]
    (let [uri (:uri request)
          params (:params request)
          url (build-endpoint api-url uri)]
      (handler (assoc request :url url :params params)))))

;; The clj-http call should be refactored eventually

;; Notes about this setup:
;; The server males hte calls and passes every appropriate header to and parameters to the client

(defn clj-request [request]
  (let [
        authorization (get-in request [:headers "authorization"])
        response (client/request {:method  (:request-method request)
                                  :url     (:url request)
                                  :headers {"authorization" authorization}
                                  :body    (:body request)
                                  :query-params (:query-params request)})]

    {:status  (:status response)
     :headers {"Content-Type" "text/html"}
     :body (:body response)}))

(defn clj-post [request]
  (let [;; request (update request :headers dissoc "content-length") ;; this is very unneccessary
       ;; _ (clojure.pprint/pprint request)
        authorization (get-in request [:headers "authorization"])
        uri (:url request)
        response (client/post uri {:form-params (:body request)
                                   :content-type :json
                                   :headers {"authorization" authorization
                                             "Accept" "application/json"}})]

    {:status  (:status response)
     :headers {"Content-Type" "application/json"}
     :body (:body response)}))

(defn clj-put [request]
  (let [authorization (get-in request [:headers "authorization"])
        uri (:url request)
        json-body (json/generate-string (:body request))
        response (client/put uri {:body  json-body
                                  :content-type :json
                                  :headers {"authorization" authorization
                                            "Accept" "application/json"}})]
    {:status (:status response)
     :headers {"Content-Type" "application/json"}
     :body (:body response)}))

(defroutes app-routes
  (GET "/" [] "Hello, Clojure!")
  (GET "/about" [] "About Clojure")
  (-> (context "/" []
        (context "/articles" []
          (GET "/" [:as request] (clj-request request))
          (POST "/" [:as request] (clj-post request))
          (PUT "/:slug" [:as request] (clj-put request))
          (GET "/feed" [:as request] (clj-request request))
          (GET "/:slug" [:as request] (clj-request request))
          (GET "/:slug/comments" [:as request] (clj-request request))
          (POST "/:slug/comments" [:as request] (clj-post request))
          ;;(GET "/author" [:as request] (clj-request request))
          )

        (GET "/tags" [:as request] (clj-request request))
        (context "/profiles" []
          (GET "/:profile" [:as request] (clj-request request)))
        (PUT "/user" [:as request] (clj-put request))
        (context "/users" []
          (POST "/" [:as request] (clj-post request))
          (POST "/login" [:as request] (clj-post request))))

      (wrap-routes  wrap-api-url))
  (OPTIONS "/*" [] (fn [_] {:status 200 :headers {} :body ""}))
  (route/not-found "Not Found at all"))

(def handler
  (-> app-routes
      (wrap-cors :access-control-allow-origin #".*" ;;[#"http://localhost:8280"] 
                 :access-control-allow-credentials "true"
                 :access-control-allow-methods [:get :put :post :delete]
                 :access-control-allow-headers ["Content-Type" "Authorization"
                                                "Origin" "Accept"])
      (wrap-params)
      (wrap-defaults (dissoc site-defaults :security))
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-reload)))
