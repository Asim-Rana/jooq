import com.jooq.model.tables.daos.AuthorDao;
import com.jooq.model.tables.pojos.Author;
import com.jooq.model.tables.pojos.Book;
import com.jooq.model.tables.records.AuthorRecord;
import com.jooq.model.tables.records.BookRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.util.List;

import static com.jooq.model.tables.Author.AUTHOR;
import static com.jooq.model.tables.Book.BOOK;

/**
 * Created by Asim on 8/26/2017.
 */
public class Main {
    public static void main(String[] args) {
        String user = "root";
        String password = "admin";
        String url = "jdbc:mysql://localhost:3306/library";

        try {
            Connection con = DriverManager.getConnection(url,user,password);
            DSLContext dsl = DSL.using(con, SQLDialect.MYSQL);

            //For DAOs
            Configuration conf = new DefaultConfiguration().set(con).set(SQLDialect.MYSQL);
            //select(dsl , conf);
            //insert(dsl, conf);
            //update(dsl, conf);
            //delete(dsl, conf);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void select(DSLContext dsl, Configuration conf) {
        // Select from Author table using Record
        Result<Record> result = dsl.select().from(AUTHOR).fetch();
        System.out.println("***********************Select Using Result<Record>**********************");
        for (Record r : result) {
            Integer id = r.getValue(AUTHOR.ID);
            String firstName = r.getValue(AUTHOR.FIRST_NAME);
            String lastName = r.getValue(AUTHOR.LAST_NAME);

            System.out.println(id + " " + firstName + " " + lastName);
        }

        // Select from Author table using AuthorRecord
        System.out.println("***********************Select Using AuthorRecord**********************");
        List<AuthorRecord> auth = dsl.selectFrom(AUTHOR).fetch();
        auth.forEach(a -> System.out.println(a.getId() + " " + a.getFirstName() + " " + a.getLastName()));

        // Select from Author,Book table using Result<Record4>
        System.out.println("***********************Result<Record4<String, String, String, Date>>**********************");
        Result<Record4<String, String, String, Date>> result2 = dsl.select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, BOOK.LANGUAGE, BOOK.PUBLISHED)
                .from(AUTHOR)
                .join(BOOK).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .where(BOOK.LANGUAGE.eq("English"))
                .and(BOOK.PUBLISHED.gt(Date.valueOf("2008-01-01")))
                .orderBy(AUTHOR.LAST_NAME.asc().nullsFirst()).fetch();

        result2.forEach(res -> System.out.println(res.getValue(AUTHOR.FIRST_NAME) + " " + res.getValue(AUTHOR.LAST_NAME) + " " + res.getValue(BOOK.LANGUAGE) + " " + res.getValue(BOOK.PUBLISHED)));

        // Select from Author table in POJO
        System.out.println("***********************Select in POJO**********************");
        List<Author> authors = dsl.select().from(AUTHOR).fetch().into(Author.class);
        authors.forEach(author -> System.out.println(author));

        // Select from Author table using DAO
        System.out.println("***********************Select using DAO**********************");
        AuthorDao authorDao = new AuthorDao(conf);
        System.out.println(authorDao.findById(1));
    }
    public static void insert(DSLContext dsl, Configuration conf) {
        // Insert into Author using DSL
        System.out.println("***********************Insert using DSL**********************");
        dsl.insertInto(AUTHOR, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME).values("Tony", "Gaaddis").execute();

        // Insert into Author using AuthorRecord
        System.out.println("***********************Insert using AuthorRecord and BookRecord**********************");
        AuthorRecord authrecord;
        authrecord = dsl.newRecord(AUTHOR);
        authrecord.setFirstName("Denis");
        authrecord.setLastName("Ritchie");
        authrecord.store();
        authrecord.refresh();

        BookRecord bookRecord;
        bookRecord = dsl.newRecord(BOOK);
        bookRecord.setAuthorId(authrecord.getId());
        bookRecord.setLanguage("English");
        bookRecord.setPublished(Date.valueOf("2017-08-19"));
        bookRecord.store();

        // Insert into Author using POJO
        System.out.println("***********************Insert using POJO**********************");
        Author author = new Author();
        author.setFirstName("Elon");
        author.setLastName("Dor");
        AuthorRecord authorRecord2 = dsl.newRecord(AUTHOR, author);
        authorRecord2.store();

        // Insert into Author using Dao
        System.out.println("***********************Insert using Dao**********************");
        AuthorDao authorDao = new AuthorDao(conf);
        Author author2 = new Author();
        author2.setFirstName("Brent");
        author2.setLastName("Wilson");
        authorDao.insert(author2);

        //After insertion
        display(dsl);
    }
    public static void update(DSLContext dsl, Configuration conf) {
        // Update Author using DSL
        System.out.println("***********************Update using DSL**********************");
        dsl.update(AUTHOR).set(AUTHOR.FIRST_NAME , "Johnny").where(AUTHOR.ID.eq(4)).execute();

        // Update using BookRecord
        System.out.println("***********************Update using BookRecord**********************");

        BookRecord bookRecord;
        bookRecord = dsl.newRecord(BOOK);
        bookRecord.setId(4);
        bookRecord.refresh();
        bookRecord.setPublished(Date.valueOf("1899-08-13"));
        bookRecord.store();

        // Insert into Author using Dao
        System.out.println("***********************Update using Dao**********************");
        AuthorDao authorDao = new AuthorDao(conf);
        Author author2 = new Author();
        author2.setId(7);
        author2.setLastName("Lee");
        authorDao.update(author2);
        //After update
        display(dsl);
    }
    public static void delete(DSLContext dsl, Configuration conf) {
        // Delete Author using DSL
        System.out.println("***********************Delete using DSL**********************");
        dsl.delete(AUTHOR).where(AUTHOR.ID.eq(4)).execute();

        // DeleteAuthor using AuthorRecord
        System.out.println("***********************Delete using AuthorRecord and BookRecord**********************");
        AuthorRecord authrecord;
        authrecord = dsl.newRecord(AUTHOR);
        authrecord.setId(6);
        authrecord.refresh();
        authrecord.delete();

        // Delete Author using Dao
        System.out.println("***********************Delete using Dao**********************");
        AuthorDao authorDao = new AuthorDao(conf);
        Author author2 = new Author();
        author2.setId(1);
        authorDao.delete(author2);

        //After insertion
        display(dsl);
    }
    public static void display(DSLContext dsl) {
        System.out.println("***********************Authors**********************");
        List<Author> authors = dsl.select().from(AUTHOR).fetch().into(Author.class);
        authors.forEach(author -> System.out.println(author));
        System.out.println("***********************Books**********************");
        List<Book> books = dsl.select().from(BOOK).fetch().into(Book.class);
        books.forEach(book -> System.out.println(book));
    }
}
