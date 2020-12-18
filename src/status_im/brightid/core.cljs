(ns status-im.brightid.core
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as string]
            [re-frame.core :as re-frame]
            [status-im.ethereum.abi-spec :as abi-spec]
            [status-im.ethereum.contracts :as contracts]
            [status-im.ethereum.core :as ethereum]
            [status-im.ethereum.eip55 :as eip55]
            [status-im.ethereum.ens :as ens]
            [status-im.ethereum.resolver :as resolver]
            [status-im.ethereum.stateofus :as stateofus]
            [status-im.multiaccounts.update.core :as multiaccounts.update]
            [status-im.signing.core :as signing]
            [status-im.navigation :as navigation]
            [status-im.utils.datetime :as datetime]
            [status-im.utils.brightid :as brightid]
            [status-im.utils.fx :as fx]
            [status-im.utils.money :as money]
            [status-im.utils.random :as random]))


;; (fx/defn return-to-brightid-main-screen
;;   {:events [::got-it-pressed ::cancel-pressed]}
;;   [{:keys [db] :as cofx} _]
;;   (fx/merge cofx
;;             ;; clear registration data
;;             {:db (dissoc db :brightid/linking)}
;;             ;; we reset navigation so that navigate back doesn't return
;;             ;; into the registration flow
;;             (navigation/navigate-reset {:index  1
;;                                         :routes [{:name :my-profile}
;;                                                  {:name :brightid-main}]})))

(re-frame/reg-fx
  :brightid/verify-context-id
  (fn [{:keys [context-id success-event error-event param]}]
    (brightid/verify-context-id context-id
                                #(re-frame/dispatch [success-event %])
                                #(re-frame/dispatch [error-event %])
                                param)))

(fx/defn on-update-brightid-success
  {:events [::update-brightid-success]}
  [{:keys [db]} response]
  {:db (assoc db :brightid/linking response)})

(fx/defn on-update-brightid-fail
  {:events [::update-brightid-fail]}
  [{:keys [db]} err]
  ;; (log/debug "Unable to get prices: " err)
  {:db (-> db :brightid/linking err)})

(fx/defn brightid-link
  {:events [::proceed-to-brightid-pressed]}
  [{:keys [db] :as cofx}]
  (let [{:keys [linked?]}
        (:brightid/linking db)
        {:keys [public-key]} (:multiaccount db)]
    {:brightid/verify-context-id
     {:context-id    public-key
      :success-event ::update-brightid-success
      :error-event   ::update-brightid-fail
      :param         {:timeout 5000}}
     :db db}))

(comment

  (comment))
