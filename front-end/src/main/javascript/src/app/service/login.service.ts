import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Endpoints} from '../core/endpoints';
import {User} from '../model/user';

@Injectable()
export class LoginService {
  private loggedIn: BehaviorSubject<User> = new BehaviorSubject<User>(null);

  constructor(private router: Router, private http: HttpClient) {
  }

  get isLoggedIn() {
    return this.loggedIn.asObservable();
  }

  login(authenticationCode: string) {
    this.http
      .post<User>(Endpoints.LOGIN, authenticationCode)
      .subscribe(
        user => this.handleLoginSuccess(user),
        error => this.handleLoginError(error)
      );
  }

  logout() {
    this.loggedIn.next(null);
    this.router.navigate(['/login']);
  }

  handleLoginSuccess(user: User) {
    this.loggedIn.next(user);
    this.router.navigate(['/']);
    console.log(user);
  }

  handleLoginError(error) {
    console.log(error);
  }
}
