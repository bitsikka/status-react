(ns status-im.utils.brightid
  (:require [status-im.utils.http :as http]))

(def default-node-url (js/encodeURIComponent "http://node.brightid.org"))
(def app-url "https://app.brightid.org")
(def context "Status")

(defn generate-deep-link
  ([context-id]
   (generate-deep-link context-id default-node-url))
  ([context-id node-url]
   (generate-deep-link context-id node-url true))
  ([context-id node-url qr?]
   (if qr?
     (str "brightid://link-verification/" node-url "/" context "/" context-id)
     (str app-url "/link-verification/" node-url "/" context "/" context-id))))

(defn verify-context-id
  [context-id on-success on-error param]
  (let [endpoint (str app-url "/node/v5/verifications/" context "/" context-id)]
    (http/get endpoint on-success on-error param)))

(comment
  (generate-deep-link "0xC0DE4C0FFEE")

  (generate-deep-link "0xC0DE4C0FFEE" default-node-url false)

  (verify-context-id
    "0x04d0116b66c1dd9ed1bdef4b8af05959580ea53512b00d03af8cacd071bcae1a132b3000e5f78482ca5fed31201c91dc73ac89e0399512c2d39a95946bfd237097"
    (fn [r] (js/console.log (js->clj r :keywordize-keys true)))
    (fn [r] (js/console.log (js->clj r :keywordize-keys true)))
    {})

  (verify-context-id
    "0x04d0116b66c1dd9ed1bdef4b8af05959580ea53512b00d03af8cacd071bcae1a132b3000e5f78482ca5fed31201c91dc73ac89e0399512c2d39a95946bfd237097"
    (fn [r] (js/console.log (.parse js/JSON r)))
    (fn [r] (js/console.log (.parse js/JSON r)))
    {})

  (verify-context-id
    "0x0474abd38bb122f3e45d71bd44aacfe8508a07561216c6d93861863fc501695f8eadd04f9567502df4788059a0fbc507d019812ff116e49ccf4653ebdaa1afdcf1"
    (fn [r] (js/console.log (.parse js/JSON r)))
    (fn [r] (js/console.log (.parse js/JSON r)))
    {})

  (verify-context-id
    "0x04ea5d593ee7b9338152cf274d4d4a2e53f37ca7db2fc6531999dedc6b5fa9e7bf77b1e5b9a8b278e72949f4c75a976bd65d70c97725168ba78d0c72dd35119402"
    (fn [r] (println (js->clj (.parse js/JSON r) :keywordize-keys true)))
    (fn [r] (println (js->clj (.parse js/JSON r) :keywordize-keys true)))
    {})

  (comment))
