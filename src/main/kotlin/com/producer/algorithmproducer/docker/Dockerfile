FROM rabbitmq:3.5.3-management


RUN rabbitmq-plugins enable --offline rabbitmq_shovel && rabbitmq-plugins enable --offline rabbitmq_shovel_management
RUN mkdir -p /usr/local/etc/rabbit

COPY ./rabbmitmq.config /usr/local/etc/rabbit/rabbit.config


