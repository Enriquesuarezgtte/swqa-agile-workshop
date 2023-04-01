package edu.upc.talent.swqa.campus.test;

import edu.upc.talent.swqa.campus.domain.CampusApp;
import edu.upc.talent.swqa.campus.domain.User;
import edu.upc.talent.swqa.campus.infrastructure.ConsoleEmailSender;
import edu.upc.talent.swqa.campus.infrastructure.PostgreSqlUsersRepository;
import edu.upc.talent.swqa.campus.infrastructure.UsersDb;
import edu.upc.talent.swqa.test.utils.DatabaseBackedTest;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class CampusAppEndToEndTest extends DatabaseBackedTest {

  private CampusApp app;
  private PostgreSqlUsersRepository repo;

  @BeforeEach
  public void setUpDatabaseSchema() {
    db.update(UsersDb.groupsTableDml);
    db.update(UsersDb.usersTableDml);
    repo = new PostgreSqlUsersRepository(db);
    repo.createGroup("swqa");
    repo.createUser("John", "Doe", "john.doe@example.com", "student", "swqa");
    repo.createUser("Jane", "Doe", "jane.doe@example.com", "student", "swqa");
    repo.createUser("Mariah", "Harris", "mariah.hairam@example.com", "teacher", "swqa");
    this.app = new CampusApp(repo, new ConsoleEmailSender());
  }

  @Test
  public void testCreateUser() {
    final var firstName = "Adria";
    final var lastName = "Doe";
    final var email = "adria.doe@example.com";
    final var role = "student";
    final var group = "swqa2";
    app.createGroup("swqa2");
    app.createUser(firstName, lastName, email, role, group);
    List<User> retrievedUsers = repo.getUsersByGroupAndRole(group, role);
    User userCreated = retrievedUsers.get(0);

    Assertions.assertEquals(firstName, userCreated.name());
    Assertions.assertEquals(lastName, userCreated.surname());
    Assertions.assertEquals(email, userCreated.email());
    Assertions.assertEquals(role, userCreated.role());
    Assertions.assertEquals(group, userCreated.groupName());

    Assertions.assertEquals(1, retrievedUsers.size());
  }

  @Test
  public void testCreateGroup() {
    app.createGroup("bigdata");

  }

  @Test
  public void testSendEmailToGroup() {
    app.sendMailToGroup("swqa", "New campus!", "Hello everyone! We just created a new virtual campus!");
  }

  @Test
  public void testSendEmailToGroupRole() {
    app.sendMailToGroupRole("swqa", "teacher", "Hey! Teacher!", "Let them students alone!!");
  }

}
