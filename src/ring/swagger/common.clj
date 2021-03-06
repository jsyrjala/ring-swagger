(ns ring.swagger.common
  (:use [clojure.walk :as walk]))

(defmacro fn->
  "Creates a function that threads on input with some->"
  [& body] `(fn [x#] (some-> x# ~@body)))

(defmacro fn->>
  "Creates a function that threads on input with some->>"
  [& body] `(fn [x#] (some->> x# ~@body)))

(defn remove-empty-keys
  "removes empty keys from a map"
  [m] (into {} (filter (fn-> second nil? not) m)))

(defn name-of
  "Returns name of a Var, String, Named object or nil"
  [x]
  (cond
    (var? x) (-> x meta :name name)
    (string? x) x
    (instance? clojure.lang.Named x) (name x)
    :else nil))

(defn value-of
  "Extracts value of for var, symbol or returns itself"
  [x]
  (cond
    (var? x) (var-get x)
    (symbol? x) (eval x)
    :else x))

(defn extract-parameters
  "Extract parameters from head of the list. Parameters can be:
     1) a map (if followed by any form) [{:a 1 :b 2} :body] => {:a 1 :b 2}
     2) list of keywords & values   [:a 1 :b 2 :body] => {:a 1 :b 2}
     3) else => {}
   Returns a tuple with parameters and body without the parameters"
  [c]
  {:pre [(sequential? c)]}
  (if (and (map? (first c)) (> (count c) 1))
    [(first c) (rest c)]
    (if (keyword? (first c))
      (let [parameters (->> c
                         (partition 2)
                         (take-while (comp keyword? first))
                         (mapcat identity)
                         (apply hash-map))
            form       (drop (* 2 (count parameters)) c)]
        [parameters form])
      [{} c])))
