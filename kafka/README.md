# Apache Kafka Setup

Este diretório contém a configuração do Apache Kafka para o projeto TechBra.

## Componentes

- **Zookeeper**: Coordenação e gerenciamento de metadados do Kafka
- **Kafka**: Broker de mensagens principal
- **Kafka UI**: Interface web para monitoramento e gerenciamento

## Como usar

### Iniciar os serviços

```bash
docker-compose up -d
```

### Parar os serviços

```bash
docker-compose down
```

### Verificar status dos serviços

```bash
docker-compose ps
```

## Portas expostas

- **Kafka**: 9092 (para aplicações)
- **Zookeeper**: 2181
- **Kafka UI**: 8080 (interface web)
- **JMX**: 9101 (monitoramento)

## Acessar Kafka UI

Após iniciar os serviços, acesse: http://localhost:8080

## Configurações importantes

- **Auto-criação de tópicos**: Habilitada
- **Retenção de logs**: 7 dias (168 horas)
- **Replicação**: Fator 1 (adequado para desenvolvimento)
- **Rede**: kafka-network (isolada)

## Comandos úteis

### Criar um tópico

```bash
docker exec -it kafka kafka-topics --create --topic meu-topico --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### Listar tópicos

```bash
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Produzir mensagens

```bash
docker exec -it kafka kafka-console-producer --topic meu-topico --bootstrap-server localhost:9092
```

### Consumir mensagens

```bash
docker exec -it kafka kafka-console-consumer --topic meu-topico --from-beginning --bootstrap-server localhost:9092
```

## Integração com microserviços

Para conectar seus microserviços ao Kafka, use:

- **Bootstrap servers**: `localhost:9092` (para aplicações rodando no host)
- **Bootstrap servers**: `kafka:29092` (para aplicações rodando em containers na mesma rede)

## Volumes

Os dados são persistidos em volumes Docker:
- `kafka-data`: Dados do Kafka
- `zookeeper-data`: Dados do Zookeeper
- `zookeeper-logs`: Logs do Zookeeper