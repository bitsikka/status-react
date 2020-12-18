(ns status-im.brightid.core
  (:refer-clojure :exclude [name])
  (:require [re-frame.core :as re-frame]
            [status-im.utils.fx :as fx]
            [status-im.multiaccounts.update.core :as multiaccounts.update]
            [status-im.utils.brightid :as bright-id]))

(re-frame/reg-fx
  :brightid/verify-context-id
  (fn [{:keys [context-id success-event error-event param]}]
    (bright-id/verify-context-id context-id
                                 #(re-frame/dispatch [success-event %])
                                 #(re-frame/dispatch [error-event %])
                                 param)))

(fx/defn on-update-brightid-success
  {:events [::update-brightid-success]}
  [cofx response]
  (fx/merge cofx
            (multiaccounts.update/multiaccount-update
              :bright-id-link-status (.parse js/JSON response)
              {})))

(fx/defn on-update-brightid-fail
  {:events [::update-brightid-fail]}
  [cofx err]
  (fx/merge cofx
            (multiaccounts.update/multiaccount-update
              :bright-id-link-error-status (.parse js/JSON err)
              {})))

(fx/defn brightid-verifications
  {:events [::proceed-to-brightid-pressed]}
  [{:keys [db]}]
  (let [{:keys [public-key]} (:multiaccount db)]
    {:db                         db
     :brightid/verify-context-id {:context-id    public-key
                                  :success-event ::update-brightid-success
                                  :error-event   ::update-brightid-fail
                                  :param         {:timeout 5000}}}))
