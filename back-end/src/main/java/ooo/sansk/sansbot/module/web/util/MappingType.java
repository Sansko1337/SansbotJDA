package ooo.sansk.sansbot.module.web.util;

import spark.Route;
import spark.Spark;

public enum MappingType {

    GET {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            Spark.get(location, responseTransformer);
        }
    },
    POST {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            Spark.post(location, responseTransformer);
        }
    },
    DELETE {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            Spark.delete(location, responseTransformer);
        }
    },
    PUT {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            Spark.put(location, responseTransformer);
        }
    },
    TRACE {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            Spark.trace(location, responseTransformer);

        }
    },
    HEAD {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            Spark.head(location, responseTransformer);
        }
    };

    public abstract void registerMapping(String location, Route responseTransformer);
}
