import {Component, ViewEncapsulation} from '@angular/core';
import {LoginService} from '../../service/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['login.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent {
  private loginService: LoginService;

  constructor(loginService: LoginService) {
    this.loginService = loginService;
  }

  login(authCode) {
    this.loginService.login(authCode);
  }
}
