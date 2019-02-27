(ns status-im.ui.screens.chat.styles.input.send-button
  (:require [status-im.ui.components.colors :as colors]))

(defn send-message-container [rotation]
  {:background-color colors/blue
   :width              30
   :height             30
   :padding-vertical   3
   :padding-horizontal 4
   :border-radius      15
   :margin             10
   :margin-left        8
   :margin-bottom      11
   :transform          [{:rotate rotation}]})
