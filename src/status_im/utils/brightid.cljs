(ns status-im.utils.brightid)

(def default-node-url (js/encodeURIComponent "http://fnode.brightid.org"))
(def app-url "https://app.brightid.org/")

(defn generate-deep-link
  ([context-id]
   (generate-deep-link context-id default-node-url))
  ([context-id node-url]
   (generate-deep-link context-id node-url true))
  ([context-id node-url qr?]
   (if qr?
     (str "brightid://link-verification/" node-url "/Status/" context-id)
     (str app-url "link-verification/" node-url "/Status/" context-id))))

(comment
  (generate-deep-link "0xC0DE4C0FFEE")

  (generate-deep-link "0xC0DE4C0FFEE" default-node-url false)

  (comment))
