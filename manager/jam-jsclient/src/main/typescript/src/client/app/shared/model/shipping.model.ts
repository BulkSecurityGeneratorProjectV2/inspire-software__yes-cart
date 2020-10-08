/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import { Pair } from './common.model';

export interface CarrierShopLinkVO {

  carrierId : number;

  shopId : number;

  disabled : boolean;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface CarrierInfoVO {

  carrierId : number;

  code  : string;

  name  : string;
  description : string;

  displayNames : Pair<string, string>[];
  displayDescriptions : Pair<string, string>[];

  carrierShops : Array<CarrierShopLinkVO>;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface CarrierVO extends CarrierInfoVO {

  slas : CarrierSlaInfoVO[];

}

export interface ShopCarrierVO extends CarrierInfoVO {

  carrierShop : CarrierShopLinkVO;

}

export interface CarrierSlaInfoVO {

  carrierslaId : number;
  carrierId : number;

  code  : string;

  name  : string;
  displayNames : Pair<string, string>[];

  description : string;
  displayDescriptions : Pair<string, string>[];

  maxDays : number;
  minDays : number;
  guaranteed : boolean;
  namedDay : boolean;

  slaType : string;

  externalRef : string;

  excludeWeekDays : string[];
  excludeDates : Pair<Date, Date>[];
  excludeCustomerTypes : string;

  script : string;

  billingAddressNotRequired : boolean;
  deliveryAddressNotRequired : boolean;

  supportedPaymentGateways : string[];
  supportedFulfilmentCentres : string[];

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface ShopCarrierSlaVO extends CarrierSlaInfoVO {

  disabled : boolean;
  rank : number;

}

export interface CarrierSlaVO extends CarrierSlaInfoVO {

}


export interface ShopCarrierAndSlaVO extends ShopCarrierVO {

  carrierSlas : ShopCarrierSlaVO[];

}

