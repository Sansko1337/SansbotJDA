import {Component} from '@angular/core';

@Component({
  selector: 'app-authenticated-layout',
  template: `
    <h1>Title</h1>
    <router-outlet></router-outlet>
  `,
  styles: []
})
export class AuthenticatedLayoutComponent {
}
