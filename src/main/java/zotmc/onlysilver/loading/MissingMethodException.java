package zotmc.onlysilver.loading;

class MissingMethodException extends RuntimeException {

  private static final long serialVersionUID = -8578461050899253705L;

  MissingMethodException(MethodPredicate target) {
    super(target.toString());
  }

}
