import {Component, ViewEncapsulation} from '@angular/core';
import {Router} from "@angular/router";
import {LoginService} from "../../service/login.service";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['sidebar.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class SidebarComponent {

  private loginService: LoginService;
  private router: Router;

  constructor(loginService: LoginService, router: Router) {
    this.loginService = loginService;
    this.router = router;
  }

  getUser() {
    return this.loginService.getUser();
  }

  navigate(route: string) {
    console.log("Navigating to: ", route);
    this.router.navigate([route]);
  }
}
