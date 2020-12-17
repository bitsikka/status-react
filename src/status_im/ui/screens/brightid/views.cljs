(ns status-im.ui.screens.brightid.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [status-im.constants :as constants]
            [status-im.ens.core :as ens]
            [status-im.ethereum.core :as ethereum]
            [status-im.ethereum.ens :as ethereum.ens]
            [status-im.ethereum.stateofus :as stateofus]
            [status-im.i18n :as i18n]
            [status-im.multiaccounts.core :as multiaccounts]
            [status-im.react-native.resources :as resources]
            [status-im.ui.components.checkbox.view :as checkbox]
            [status-im.ui.components.colors :as colors]
            [status-im.ui.components.common.common :as components.common]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.topbar :as topbar]
            [status-im.ui.screens.chat.utils :as chat.utils]
            [status-im.ui.components.toolbar :as toolbar]
            [status-im.ui.screens.chat.message.message :as message]
            [status-im.ui.screens.chat.photos :as photos]
            [status-im.ui.screens.profile.components.views :as profile.components]
            [status-im.utils.debounce :as debounce]
            [clojure.string :as string]
            [status-im.ethereum.tokens :as tokens]
            [quo.core :as quo])
  (:require-macros [status-im.utils.views :as views]))

(views/defview main []
  (views/letsubs [{:keys [names multiaccount show? registrations]} [:ens.main/screen]]
                 [react/keyboard-avoiding-view {:style {:flex 1}}
                  [topbar/topbar {:title (i18n/label :t/ens-usernames)}]
                  (if (or (seq names) registrations)
                    [registered names multiaccount show? registrations]
                    [welcome])]))
