package tfa.tickets.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Represents a user with sort and full name, plus email address
 * (can be loaded from LDAP in future : uidNumber, uid, cn, mail)
 */
@Entity
public class User implements Serializable
{
  private static final long serialVersionUID = -3201183850758755648L;

  // max lengths for strings and names
  public static final int nameLength = 32;
  private static final int fullLength = 64;
  private static final int mailLength = 64;

  // ---------------------------------------------- entity columns

  @Id
  @Column(columnDefinition = "VARCHAR", nullable = false, length = nameLength)
  private String ident;

  @Column(columnDefinition = "VARCHAR", nullable = false, length = fullLength)
  private String fullName;

  @Column(columnDefinition = "VARCHAR", nullable = false, length = mailLength)
  private String mail;

  // ----------------------------------------------- constructors

  public User()
  {
    super();
  }

  public User(String id, String full, String email)
  {
    this.ident = id;
    this.fullName = full;
    this.mail = email;
  }

  // ---------------------------------------------------- utility

  @Override
  public String toString()
  {
    return fullName;
  }

  // --------------------------------------------- setters - getters

  public String getIdent()
  {
    return ident;
  }

  public void setIdent(String ident)
  {
    this.ident = ident;
  }

  public String getFullName()
  {
    return fullName;
  }

  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }

  public String getMail()
  {
    return mail;
  }

  public void setMail(String mail)
  {
    this.mail = mail;
  }

}
