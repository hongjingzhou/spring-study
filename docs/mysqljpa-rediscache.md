# Spring JPA for MySql and Redis based Spring Cache
1. 使用 Spring JPA 操作 MySql 数据库
2. 使用基于 Redis 的 Spring Cache 对 MySql 数据库操作进行缓存支持

Note:  
```
IDE: IntelliJ   
语言: Kotlin 1.3.50   
Spring-boot 版本: 2.2.1.RELEASE
```


### 1. 创建工程
Go to https://start.spring.io/ 添加如下 Dependencies 后生成 Spring 工程文件  
     Spring Web / MySQL Driver / Spring Data JPA / Spring Data Redis / Spring cache abstraction  

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.1.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>demo</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
		<kotlin.version>1.3.50</kotlin.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.module</groupId>
			<artifactId>jackson-module-kotlin</artifactId>
		</dependency>
		<dependency>
			<groupId>org.jetbrains.kotlin</groupId>
			<artifactId>kotlin-reflect</artifactId>
		</dependency>
		<dependency>
			<groupId>org.jetbrains.kotlin</groupId>
			<artifactId>kotlin-stdlib-jdk8</artifactId>
		</dependency>

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
	</dependencies>

	<build>
		<sourceDirectory>${project.basedir}/src/main/kotlin</sourceDirectory>
		<testSourceDirectory>${project.basedir}/src/test/kotlin</testSourceDirectory>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.jetbrains.kotlin</groupId>
				<artifactId>kotlin-maven-plugin</artifactId>
				<configuration>
					<args>
						<arg>-Xjsr305=strict</arg>
					</args>
					<compilerPlugins>
						<plugin>spring</plugin>
						<plugin>jpa</plugin>
					</compilerPlugins>
				</configuration>
				<dependencies>
					<dependency>
						<groupId>org.jetbrains.kotlin</groupId>
						<artifactId>kotlin-maven-allopen</artifactId>
						<version>${kotlin.version}</version>
					</dependency>
					<dependency>
						<groupId>org.jetbrains.kotlin</groupId>
						<artifactId>kotlin-maven-noarg</artifactId>
						<version>${kotlin.version}</version>
					</dependency>
				</dependencies>
			</plugin>
		</plugins>
	</build>

</project> 
```     

### 2. 配置 MySQL / Redis
```

spring:
  application:
    name: jpacache

  redis:
    database: 2
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}

  datasource:
    name: default
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/jpacache?useUnicode=true&characterEncoding=UTF8&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    show-sql: true
    hibernate:
      ddl-auto: update
```

### 3. 编写Entity类
```
@Entity
@Table(name = "users")
class User : Serializable {
    @Id
    @Column(name = "email", nullable = false, unique = true)
    var email: String = ""

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "avatar", nullable = true)
    var avatar: String? = null

    @Column(name = "age", nullable = true)
    var age: Int? = null
}
```

### 4. 编写 JPA UserRepository 接口
```
interface UserRepository : JpaRepository<User, String> {
    @Query(value = "select * from users where age > :age", nativeQuery = true)
    fun listOldGuys(@Param("age") age: Int) : List<User>
}
```

### 5. 操作代码
```
@Component
class DbOp {
    @Autowired
    lateinit var userRepo: UserRepository

    fun get(email: String) : User? {
        return userRepo.findByIdOrNull(email)
    }

    fun add(email: String, name: String, avatar: String?) : User {
        return userRepo.save(User(email, name, avatar, null))
    }

    fun update(email: String, name: String, avatar: String?, age: Int?) : User {
        return userRepo.save(User(email, name, avatar, age))
    }

    fun list() : List<User> {
        return userRepo.findAll()
    }

    fun delete(email: String) {
        userRepo.deleteById(email)
    }

    fun listOldGuys(age: Int) : List<User> {
        return userRepo.listOldGuys(age)
    }
}
```

### 6. 配置redis支持spring cache
```
@Configuration
@EnableCaching
class CacheConfiguration {

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val redisCacheConfiguration = RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofDays(1))

        return RedisCacheManager
            .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
            .cacheDefaults(redisCacheConfiguration)
            .build()
    }
}
```

### 7. 在需要缓存的数据库操作上添加cache注解
```

@Component
class DbOp {
    @Autowired
    lateinit var userRepo: UserRepository

    @Cacheable(value = ["user"], key = "#email")
    fun get(email: String) : User? {
        return userRepo.findByIdOrNull(email)
    }

    @CachePut(value = ["user"], key = "#email")
    fun add(email: String, name: String, avatar: String?) : User {
        return userRepo.save(User(email, name, avatar, null))
    }

    @CachePut(value = ["user"], key = "#email")
    fun update(email: String, name: String, avatar: String?, age: Int?) : User {
        return userRepo.save(User(email, name, avatar, age))
    }

    fun list() : List<User> {
        return userRepo.findAll()
    }

    @CacheEvict(value = ["user"], key = "#email", beforeInvocation = true)
    fun delete(email: String) {
        userRepo.deleteById(email)
    }

    fun listOldGuys(age: Int) : List<User> {
        return userRepo.listOldGuys(age)
    }
}

```

Done.
完整示例代码 https://github.com/hongjingzhou/spring-study/tree/master/jpacache

