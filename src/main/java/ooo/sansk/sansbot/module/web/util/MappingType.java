package ooo.sansk.sansbot.module.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Route;
import spark.Spark;

public enum MappingType {

    GET {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            super.registerMapping(location, responseTransformer);
            Spark.get(location, responseTransformer);
        }
    },
    POST {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            super.registerMapping(location, responseTransformer);
            Spark.post(location, responseTransformer);
        }
    },
    DELETE {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            super.registerMapping(location, responseTransformer);
            Spark.delete(location, responseTransformer);

        }
    },
    PUT {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            super.registerMapping(location, responseTransformer);
            Spark.put(location, responseTransformer);

        }
    },
    TRACE {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            super.registerMapping(location, responseTransformer);
            Spark.trace(location, responseTransformer);

        }
    },
    HEAD {
        @Override
        public void registerMapping(String location, Route responseTransformer) {
            super.registerMapping(location, responseTransformer);
            Spark.head(location, responseTransformer);
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(MappingType.class);

    public void registerMapping(String location, Route responseTransformer) {
        logger.info("Registering [{}] {}", name(), location);
    }
}
