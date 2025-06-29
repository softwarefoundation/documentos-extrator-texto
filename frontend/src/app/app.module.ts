import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CardModule} from "primeng/card";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from 'primeng/button';
import {HttpClientModule} from "@angular/common/http";
import {TableModule} from 'primeng/table';
import {NgxExtendedPdfViewerModule} from "ngx-extended-pdf-viewer";
import {ExamplePdfViewerComponent} from "./example-pdf-viewer/example-pdf-viewer.component";
import {DialogModule} from "primeng/dialog";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CardModule,
    FormsModule,
    InputTextModule,
    ButtonModule,
    HttpClientModule,
    TableModule,
    NgxExtendedPdfViewerModule,
    ExamplePdfViewerComponent,
    DialogModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
