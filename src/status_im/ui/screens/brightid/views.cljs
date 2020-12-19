(ns status-im.ui.screens.brightid.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [status-im.utils.brightid :as utils.brightid]
            [status-im.brightid.core :as brightid]
            [status-im.i18n :as i18n]
            [status-im.react-native.resources :as resources]
            [status-im.ui.components.colors :as colors]
            [status-im.ui.components.copyable-text :as copyable-text]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.topbar :as topbar]
            [status-im.ui.components.qr-code-viewer.views :as qr-code-viewer]
            [status-im.utils.debounce :as debounce]
            [quo.core :as quo])
  (:require-macros [status-im.utils.views :as views]))

(views/defview link-brightid-to-chat-key []
  (views/letsubs [{:keys [address]} [:popover/popover]
                  width (reagent/atom nil)]
                 (let [deep-link (utils.brightid/generate-deep-link address)]
                   [react/view {:on-layout
                                #(reset! width
                                         (-> ^js % .-nativeEvent .-layout .-width))}
                    [react/view {:style {:padding-top 16 :padding-horizontal 16}}
                     (when @width
                       [qr-code-viewer/qr-code-view (- @width 32) deep-link])
                     [copyable-text/copyable-text-view
                      {:label           :t/brightid-deep-link
                       :container-style {:margin-top 12 :margin-bottom 16}
                       :copied-text     deep-link}
                      [quo/text {:number-of-lines     1
                                 :ellipsize-mode      :middle
                                 :accessibility-label :chat-key
                                 :monospace           true}
                       deep-link]]]])))

(defn- link-again[]
  (let [address  (:public-key @(re-frame/subscribe [:multiaccount]))
        on-scaan (fn []
                   (re-frame/dispatch
                     [:show-popover
                      {:view    :link-brightid-to-chat-key
                       :address address}])
                   (debounce/debounce-and-dispatch [::brightid/proceed-to-brightid-pressed] 60000))]
    [react/view {:style {:flex 1}}
     [react/scroll-view {:content-container-style {:align-items :center}}
      [react/text {:style {:margin-top 32 :margin-bottom 8 :typography :header}}
       (i18n/label :t/brightid-link-chat-key)]
      [react/text {:style {:margin-top 8 :margin-bottom 24 :color colors/gray :font-size 15 :margin-horizontal 16 :text-align :center}}
       "You are verified by brightID, but in Status you have another account you linked recently.\n\nYou can choose to link again with this acccount here, or switch to the other account and use it as your main account."]
      [quo/button {:type     :primary
                   :theme    :accent
                   :after    :main-icon/next
                   :on-press (fn []
                               (.openURL ^js react/linking
                                         (utils.brightid/generate-deep-link address))
                               (debounce/debounce-and-dispatch [::brightid/proceed-to-brightid-pressed] 60000))}
       (i18n/label :t/brightid-proceed-to-app)]
      [react/text {:style {:margin-top 8 :margin-bottom 8 :color colors/gray :font-size 15 :margin-horizontal 16 :text-align :center}}
       (i18n/label :t/or)]
      [quo/button {:type :primary :theme :accent :after :main-icon/next :on-press on-scaan} (i18n/label :t/brightid-scan-qr-code)]
      [react/text {:style {:margin-top 16 :text-align :center :color colors/gray :typography :caption :padding-bottom 96}}
       (i18n/label :t/brightid-powered-by)]]]))

(defn- welcome []
  (let [address  (:public-key @(re-frame/subscribe [:multiaccount]))
        on-scaan (fn []
                   (re-frame/dispatch
                     [:show-popover
                      {:view    :link-brightid-to-chat-key
                       :address address}])
                   (debounce/debounce-and-dispatch [::brightid/proceed-to-brightid-pressed] 60000))]
    [react/view {:style {:flex 1}}
     [react/scroll-view {:content-container-style {:align-items :center}}
      [react/text {:style {:margin-top 32 :margin-bottom 8 :typography :header}}
       (i18n/label :t/brightid-link-chat-key)]
      [react/text {:style {:margin-top 8 :margin-bottom 24 :color colors/gray :font-size 15 :margin-horizontal 16 :text-align :center}}
       (i18n/label :t/brightid-welcome-hints)]
      [quo/button {:type     :primary
                   :theme    :accent
                   :after    :main-icon/next
                   :on-press (fn []
                               (.openURL ^js react/linking
                                         (utils.brightid/generate-deep-link address))
                               (debounce/debounce-and-dispatch [::brightid/proceed-to-brightid-pressed] 60000))}
       (i18n/label :t/brightid-proceed-to-app)]
      [react/text {:style {:margin-top 8 :margin-bottom 8 :color colors/gray :font-size 15 :margin-horizontal 16 :text-align :center}}
       (i18n/label :t/or)]
      [quo/button {:type :primary :theme :accent :after :main-icon/next :on-press on-scaan} (i18n/label :t/brightid-scan-qr-code)]
      [react/text {:style {:margin-top 16 :text-align :center :color colors/gray :typography :caption :padding-bottom 96}}
       (i18n/label :t/brightid-powered-by)]]]))

(defn main []
  (let [{:keys [brightid-link-status
                brightid-link-error-status]}
        @(re-frame/subscribe [:multiaccount])
        brightid-verified (when brightid-link-status
                            (-> (js->clj
                                  brightid-link-status :keywordize-keys true)
                                :data))
        brightid-error    (when brightid-link-error-status
                            (:errorNum
                             (js->clj
                               brightid-link-error-status :keywordize-keys true)))]
    [react/keyboard-avoiding-view {:style {:flex 1}}
     [topbar/topbar {:title (i18n/label :t/brightid)}]
     (if brightid-verified
       (if (:unique brightid-verified)
         [react/view {:style {:flex 1}} [react/text {:style {:margin-top 8 :margin-bottom 24 :color colors/gray :font-size 15 :margin-horizontal 16 :text-align :center}} "Verified!"]]
         [link-again])
       (if brightid-error
         (condp = brightid-error
           2 [welcome]
           3 [react/view {:style {:flex 1}} [react/text {:style {:margin-top 8 :margin-bottom 24 :color colors/gray :font-size 15 :margin-horizontal 16 :text-align :center}} "BrightID is linked with your Chat key, but you are yet to be verified as a unique individual by brightID.\n\nJoin a verifying party held by BrightID at various time of every day of the week."]]
           4 [react/view {:style {:flex 1}} [react/text {:style {:margin-top 8 :margin-bottom 24 :color colors/gray :font-size 15 :margin-horizontal 16 :text-align :center}} "BrightID is linked with your Chat key, but you are yet to be sponsored by an app that uses brightID.\n\nPlease link and get sponsored in other apps and come back. 1hive and rabbithole.gg are examplees of apps you can get sponsored in."]]
           [welcome])
         [welcome]))]))
