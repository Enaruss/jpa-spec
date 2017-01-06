[![Build Status](https://travis-ci.org/wenhao/jpa-spec.svg?branch=master)](https://travis-ci.org/wenhao/jpa-spec)

# jpa-spec

<!--
Inspired by [Legacy Hibernate Criteria Queries], while this should be considered deprecated vs JPA APIs,

but it still productive and easily understandable. Build on Spring Data JPA and simplify the dynamic query process.
-->
灵感来自于[Legacy Hibernate Criteria Queries]，这个功能在 JAP 出来之后被 Hibernate 废弃了。

但是用起来还是非常简单和高效的。此库构建与 Spring Data JPA 之上并简化了数据库动态查询。

<!--
### Features
-->
### 功能

<!--
* Compatible with Spring Data JPA and JPA 2.1 interface.
* Equal/NotEqual/Like/NotLike/In/NotIn support multiple values, Equal/NotEqual support **Null** value.
* Each specification support join query(left joiner). 
* Support custom specification.
* Builder style specification creator.
* Support pagination and sort builder.
-->
* 兼容 Spring Data JAP 和 JPA 2.1 接口。
* Equal/NotEqual/Like/NotLike/In/NotIn 支持多参数, Equal/NotEqual 支持 **Null** 值。
* 每个条件查询支持关联查询(左连接)。
* 支持自定义条件查询。
* 条件查询构建器。
* 支持分页和排序。

### Gradle

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.wenhao:jpa-spec:3.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.wenhao</groupId>
    <artifactId>jpa-spec</artifactId>
    <version>3.0.0</version>
</dependency>
```

<!--
### Specification By Examples
-->
### 条件查询例子

<!--
####Each specification support three parameters:
-->
####每个条件查询支持三个参数:

<!--
1. condition: if true(default), apply this specification.
2. property: field name.
3. values: compare value with model, eq/ne/like support multiple values.
-->
1. **condition**: 如果为`true`(默认)，应用此条件查询。
2. **property**: 字段名称。
3. **values**: 具体查询的值，eq/ne/like 支持多个值。

<!--
####General Example
-->
####例子

<!--
each Repository class should extends from two super class **JpaRepository** and **JpaSpecificationExecutor**.
-->
每个 Repository 类需要继承两个类 **JpaRepository** 和 **JpaSpecificationExecutor**。

```java
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
}    
```

```java
public Page<Person> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .eq(StringUtils.isNotBlank(request.getName()), "name", request.getName())
            .gt(Objects.nonNull(request.getAge()), "age", 18)
            .between("birthday", new Range<>(new Date(), new Date()))
            .like("nickName", "%og%", "%me")
            .build();
            
    return personRepository.findAll(specification, new PageRequest(0, 15)); 
}
```

<!--
####Equal/NotEqual Example
-->
####Equal/NotEqual例子

<!--
find any person nickName equals to "dog" and name equals to "Jack"/"Eric" or null value, and company is null.
-->
查询任何昵称等于 "dog"，名字等于 "Jack"/"Eric"或为null并且公司也为null的人。

<!--
**Test:** [EqualTest.java] and [NotEqualTest.java]
-->
**Test:** [EqualTest.java] 和 [NotEqualTest.java]

```java
public List<Person> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .eq("nickName", "dog")
            .eq(StringUtils.isNotBlank(request.getName()), "name", "Jack", "Eric", null)
            .eq("company", null) //or eq("company", (Object) null)
            .build();
            
    return personRepository.findAll(specification); 
}
```

<!--
####In/NotIn Example
-->
####In/NotIn例子

<!--
find any person name in "Jack" or "Eric" and company not in "ThoughtWorks" or "IBM".
-->
查询任何名字等于 "Jack" 或 "Eric" 并且公司不等于 "ThoughtWorks" 或 "IBM" 的人。

**Test:** [InTest.java]

```java
public List<Person> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .in("name", request.getNames().toArray()) //or in("name", "Jack", "Eric")
            .notIn("company", "ThoughtWorks", "IBM")
            .build();
            
    return personRepository.findAll(specification); 
}
```

<!--
####Numerical Example
-->
####数字比较例子

<!--
find any people age bigger than 18. 
-->
查询任何年纪大于等于18的人。

**Test:** [GtTest.java]

```java
public List<Person> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .gt(Objects.nonNull(request.getAge()), "age", 18)
            .build();
            
    return personRepository.findAll(specification); 
}
```

<!--
####Between Example
-->
####Between例子

<!--
find any person age between 18 and 25, birthday between someday and someday.
-->
查询任何年龄在18到25，生日在某个时间段的人。

**Test:** [BetweenTest.java]

```java
public List<Person> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .between(Objects.nonNull(request.getAge(), "age", new Range<>(18, 25))
            .between("birthday", new Range<>(new Date(), new Date()))
            .build();
            
    return personRepository.findAll(specification); 
}  
```

<!--
####Like/NotLike Example
-->
####Like/NotLike例子

<!--
find any person name like %ac% or %og%, company not like %ec%.
-->
查询任何名字包含  %ac% 或 %og%，公司不包含 %ec% 的人。

<!--
**Test:** [LikeTest.java] and [NotLikeTest.java]
-->
**Test:** [LikeTest.java] 和 [NotLikeTest.java]

```java
public Page<Person> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .like("name", "ac", "%og%")
            .notLike("company", "ec")
            .build();
            
    return personRepository.findAll(specification); 
}
```

<!--
####Or
-->
####Or例子

<!--
support or specifications.
-->
支持或条件查询。

**Test:** [OrTest.java]

```java
public List<Phone> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .and(OrSpecifications.<Person>builder()
                    .like("name", "%ac%")
                    .gt("age", 19)
                    .build())
            .build();

    return phoneRepository.findAll(specification);
}
```

<!--
####Join
-->
####关联查询

<!--
each specification support association query as left join. 
-->
每个条件查询都支持左连接查询。

**Test:** [JoinTest.java]

<!--
@ManyToOne association query, find person name equals to "Jack" and phone brand equals to "HuaWei".
-->
多对一查询，查询任何名字等于 "Jack" 并且此人的电话品牌是 "HuaWei"的人。
    
```java
public List<Phone> findAll(SearchRequest request) {
    Specification<Phone> specification = Specifications.<Phone>builder()
        .eq(StringUtils.isNotBlank(request.getBrand()), "brand", "HuaWei")
        .eq(StringUtils.isNotBlank(request.getPersonName()), "person.name", "Jack")
        .build();

    return phoneRepository.findAll(specification);
}
```

<!--
@ManyToMany association query, find person age between 10 and 35, live in "Chengdu" street.
-->
多对多查询，查询任何年龄在10到35之间并且其地址在 "Chengdu" 的人。

```java
public List<Phone> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
        .between("age", new Range<>(10, 35))
        .eq(StringUtils.isNotBlank(jack.getName()), "addresses.street", "Chengdu")
        .build();

    return phoneRepository.findAll(specification);
}
```

<!--
####Custom Specification
-->
####自定义条件查询

<!--
You can custom specification to do the @ManyToOne and @ManyToMany as well.
-->
你也可以自定义条件查询来实现多对一和多对多查询。

<!--
@ManyToOne association query, find person name equals to "Jack" and phone brand equals to "HuaWei".
-->
多对一查询，查询任何名字等于 "Jack" 并且此人的电话品牌是 "HuaWei"的人。

**Test:** [AndTest.java]

```java
public List<Phone> findAll(SearchRequest request) {
    Specification<Phone> specification = Specifications.<Phone>builder()
        .eq(StringUtils.isNotBlank(request.getBrand()), "brand", "HuaWei")
        .and(StringUtils.isNotBlank(request.getPersonName()), (root, query, cb) -> {
            Path<Person> person = root.get("person");
            return cb.equal(person.get("name"), "Jack");
        })
        .build();

    return phoneRepository.findAll(specification);
}
```

<!--
@ManyToMany association query, find person age between 10 and 35, live in "Chengdu" street.
-->
多对多查询，查询任何年龄在10到35之间并且其地址在 "Chengdu" 的人。

**Test:** [AndTest.java]

```java
public List<Phone> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
        .between("age", new Range<>(10, 35))
        .and(StringUtils.isNotBlank(jack.getName()), ((root, query, cb) -> {
            Join address = root.join("addresses", JoinType.LEFT);
            return cb.equal(address.get("street"), "Chengdu");
        }))
        .build();

    return phoneRepository.findAll(specification);
}
```

<!--
####Pagination and Sort
-->
####分页和排序

<!--
find person by pagination and sort by name desc and birthday asc.
-->
分页并按照名字倒序生日升序查询。

```java
public Page<Person> findAll(SearchRequest request) {
    Specification<Person> specification = Specifications.<Person>builder()
            .eq(StringUtils.isNotBlank(request.getName()), "name", request.getName())
            .gt("age", 18)
            .between("birthday", new Range<>(new Date(), new Date()))
            .like("nickName", "%og%")
            .build();
            
    Sort sort = Sorts.builder()
        .desc(StringUtils.isNotBlank(request.getName()), "name")
        .asc("birthday")
        .build();
            
    return personRepository.findAll(specification, new PageRequest(0, 15, sort));
}
```

<!--
####Virtual View
-->
####虚拟视图

<!--
Using **@org.hibernate.annotations.Subselect** to define a virtual view if you don't want a database table view.
-->
如果你不想使用数据库视图(数据库依赖)，可以 **@org.hibernate.annotations.Subselect** 虚拟视图代替(灵活修改/提升可读性)。

<!--
There is no difference between a view and a database table for a Hibernate mapping.
-->
对于 Hibernate 映射来说虚拟视图和数据库视图没任何区别。

**Test:** [VirtualViewTest.java]

```java
@Entity
@Immutable
@Subselect("SELECT p.id, p.name, p.age, ic.number " +
           "FROM person p " +
           "LEFT JOIN id_card ic " +
           "ON p.id_card_id=ic.id")
public class PersonIdCard {
    @Id
    private Long id;
    private String name;
    private Integer age;
    private String number;
    
    // Getters and setters are omitted for brevity
}    
```

```java
public List<PersonIdCard> findAll(SearchRequest request) {
    Specification<PersonIdCard> specification = Specifications.<PersonIdCard>builder()
            .gt(Objects.nonNull(request.getAge()), "age", 18)
            .build();
            
    return personIdCardRepository.findAll(specification); 
}
```

<!--
####Projection, GroupBy, Aggregation
-->
####投射、分组和聚合

<!--
Spring Data JPA doesn't support **Projection**(a little but trick), **GroupBy** and **Aggregation**, 

furthermore, Projection/GroupBy/Aggregation are often used for complex statistics report, it might seem like overkill to use Hibernate/JPA ORM to solve it.

Alternatively, using virtual view and give a readable/significant class name to against your problem domain may be a better option.
-->
Spring Data JPA对投射、分组和聚合支持不是很好，

此外，投射、分组和聚合支大多数用在比较复杂的统计报表或性能要求比较高的查询，如果使用 Hibernate/JPA 来对象关系映射来解决可能有点过于复杂了。

或者，使用虚拟视图并给一个易读的、有意义的类名来解决特定的问题也许是一个不错的选择。

### Copyright and license

Copyright 2016 Wen Hao

Licensed under [Apache License]

[Legacy Hibernate Criteria Queries]: https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#appendix-legacy-criteria
[EqualTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/EqualTest.java 
[NotEqualTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/NotEqualTest.java 
[InTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/InTest.java
[GtTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/GtTest.java
[BetweenTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/BetweenTest.java
[LikeTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/LikeTest.java
[NotLikeTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/NotLikeTest.java
[OrTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/OrTest.java
[AndTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/AndTest.java
[JoinTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/JoinTest.java
[VirtualViewTest.java]: ./src/test/java/com/github/wenhao/jpa/integration/VirtualViewTest.java
[Apache License]: ./LICENSE