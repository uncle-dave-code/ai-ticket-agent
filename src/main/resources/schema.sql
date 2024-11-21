CREATE TABLE flights (
                         id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                         origin VARCHAR(50) NOT NULL,
                         destination VARCHAR(50) NOT NULL,
                         date DATE NOT NULL
);

CREATE TABLE customers (
                           id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           email VARCHAR(100) NOT NULL
);

CREATE TABLE reservations (
                              id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                              customer_id UUID REFERENCES customers(id) ON DELETE CASCADE,
                              flight_id UUID REFERENCES flights(id) ON DELETE CASCADE,
                              status VARCHAR(20) CHECK (status IN ('confirmed', 'canceled')) NOT NULL
);