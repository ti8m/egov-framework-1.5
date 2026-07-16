## [1.9.0](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0...v1.9.0) (7/13/2026)


### Features

* **bom:** add e-gov-luid to bom ([42a890e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/42a890e5349ded1623f71d4f3b4dfd1dd89dc8a4))
* **command:** add app version ([15bbab9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/15bbab97209fa79d34d34bb16caf0e220b40d257))
* **command:** allow domain command factory to be customized ([1988d69](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1988d69d9b51faa3a976a2c52d24165575bbdfd1))
* **command:** allow domain setting via action configuration ([5f25ddd](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5f25ddd0b898b242e30f6beac7c7d9c3a420259a))
* **command:** make parent command load lazily ([94a53bb](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/94a53bb0f1c4f5029ffefe50856c58bbc7f524ab))
* **command:** make value size adaptable ([f15e9a4](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f15e9a40e7c305148c8957c98ab0cd12c0c59458))
* **exception:** customize exception output through generic handler ([e0b0c7e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e0b0c7ec54013f98460a3096d8de87aea1b828c2))
* **filter:** add option for db-name filter ([9f8a16a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/9f8a16a1966a9c0386cc975b4d137c576b450ae0)), closes [TIMSBB-161](https://jira.ti8m.ch/browse/TIMSBB-161)
* **luid:** add luid generator ([2f23ace](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/2f23ace206a6a5e0500016390ffd08f15b42f58e)), closes [TIMSBB-179](https://jira.ti8m.ch/browse/TIMSBB-179)
* **mdm:** add default master data management functionalities ([53024ad](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/53024adc3987b9e5fdf704c69573cefa00c668c3)), closes [TIMSBB-170](https://jira.ti8m.ch/browse/TIMSBB-170)
* **mdm:** add default master data management functionalities ([13046e9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/13046e986cad2fd23e35735125bc59d56fb62100)), closes [TIMSBB-170](https://jira.ti8m.ch/browse/TIMSBB-170)
* **mdm:** CURIAPLUS-13786 api with get tests and a feature to validate short- and long-name individually. ([4e4c367](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/4e4c3676091e521508bc7f2d3034bc33c4976124))
* **mdm:** CURIAPLUS-13786 missing constructors and POST test with member attributes. ([4cd4c42](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/4cd4c423c81e1d4001b30730f3f06ed3a184fe12))
* **mdm:** stabelize additional content ([326e2f2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/326e2f234adcabcf49338c4709f7dd894606c8c0)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)
* **persistence:** add methods to query user permissions ([b25b094](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/b25b0949c0139ddb2cb4e9ee43df816828b44603))
* **persistence:** allow IDs to be int and long for specific methods. this is... ([722908e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/722908e458b03f306335ad014edee3170fa80959))
* **persistence:** CURIAPLUS-13284 only use @Transactional for modifying methods ([92558ab](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/92558ab43051f941ee07ebc44b4a995b3b7a0e06))
* **persistence:** dont add ids twice, already added in save() ([3b14fec](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/3b14fecf002a72f64360e15bb3358de89269adf8))
* **persistence:** drop manual transaction management ([1e986a5](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1e986a53880a64f59dea96fa2b6d16286515a128))
* **persistence:** ensure usage of same classloader ([ca5befa](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ca5befa682711ef549088dbafe69deffe72e4f94))
* **persistence:** findAllById returns empty list for empty input ([3483a4b](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/3483a4b94f987eec21eb05c59b2d18169e4c5e8d))
* **persistence:** fix order/pagination calculation ([e455d63](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e455d636c3f09547e3b8d26249ec09a675d87bb8))
* **persistence:** ignore StreamingResponseBody in serialization ([e350962](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e350962f1d84bb91e1be1da734df61822457a81a))
* **persistence:** prefix table for db column names ([b84f22b](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/b84f22b2e6ce14bb0569be4ccc92aedd47f5cbdc))
* **persistence:** streamline tx handling ([0a168a6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0a168a6f69956d4b047dcd4fa1b571ce168cd431))
* **persistence:** switch archived filter to filter for 'not in archived=true' ([9a6e1a2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/9a6e1a29dcdd3c2ba642031fe3c5b77dca86defb))
* **persistence:** switch archived filter to filter for <not in archived=true> ([0a45e71](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0a45e71bafa4794af8ee4cb1affda9a9191f32de))
* **persistence:** unproxy classname in stacktrace walk ([e795dea](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e795dea174629e07c556905435f88298ea583054))
* **persistence:** update order and pagination setting to respect primary repository ([ee53db2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ee53db28c642f2a557417451ab1a9bf6bc3caac3))
* **persistence:** update primary repository calculation ([011a54f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/011a54f130b5311ced6f208db2040ad567ea2391))
* **pipeline:** TIMSBB-315 set testcontainer variables, init des egov... ([ccd2ed6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ccd2ed68a1aa4467845bce17595ee4c63a9df07a)), closes [TIMSBB-315](https://jira.ti8m.ch/browse/TIMSBB-315)
* **post-action:** make postaction orignal-command aware ([f3b7207](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f3b72076356aa3b3ae2706596221dcb2af8e4f0f))
* **rci:** enable custom object mapper for rest template ([782a35a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/782a35a9563731e823421a8b323e9244b72af077))
* **rci:** enable header/resttemplate customization for rci ([dd9c1a9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/dd9c1a9db8f8644a0b46b8d8e59696a132ddffaf))
* **rci:** fix bean names ([1133fe3](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1133fe375eb9b56e1c6a417e3dae657b6e1465f0))
* **rci:** inject object mapper config ([8dc8972](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/8dc8972b86c6fa266810bd0287b556f706be5632))
* **rci:** reduce excessive logging ([f29b5cc](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f29b5cc62f12a91c55ad6ca56e7d9530ce2032f0))
* **rci:** throw exceptions on remote failure ([0dae2ca](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0dae2ca0f296c2e2898eacbc29cefc1ececa2a6f))
* **rci:** use jackson for response deserialization ([5e4f0f6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5e4f0f6cd4bc8078d0e04326bc0741079dbbb4a1)), closes [TIMSBB-180](https://jira.ti8m.ch/browse/TIMSBB-180)
* **reflection-proxy-unwrap:** initialize + unwrap hibernate proxy to prevent uninitialized value ([9762081](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/976208191d911fbc794fb4a79182201febd9cb5e))
* **release:** stabalise release pipeline ([94c35d4](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/94c35d4a6bebbe6392666aa998de3fb131849912))
* **search:** make fulltext search respond to database type ([53c9bb7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/53c9bb7fae49f8487b0e07085f852ef3e7afc44e))
* **service-discovery:** add zookeeper authentication ([a96093d](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/a96093d5d5889ce7329ccc7c09ea31f29f9e5d01))
* **service-discovery:** add zookeeper standing connection ([d4d25a6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d4d25a67f6b00f114752c1b7b2ffcaac91a9b482)), closes [TIMSBB-137](https://jira.ti8m.ch/browse/TIMSBB-137)
* **subscriber:** update tx logic, add configurable pool size for subscriber execution ([a399de7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/a399de7330a4ad04b5971747a6272831c0257622)), closes [TIMSBB-202](https://jira.ti8m.ch/browse/TIMSBB-202)
* **version:** update version ([f7afe9b](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f7afe9ba253d7fbd6254dbf2f6b1aaf805bf7265))


### Bug Fixes

* **persistence:** preserve Parameters.params over the wire and on disk ([bd43254](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/bd432541cea47e3d44b7e2e50b4820b9080a0b2a))

## [1.9.0-develop.46](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.45...v1.9.0-develop.46) (6/5/2026)


### Bug Fixes

* **persistence:** preserve Parameters.params over the wire and on disk ([bd43254](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/bd432541cea47e3d44b7e2e50b4820b9080a0b2a))

## [1.9.0-develop.45](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.44...v1.9.0-develop.45) (5/26/2026)


### Features

* **pipeline:** TIMSBB-315 set testcontainer variables, init des egov... ([ccd2ed6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ccd2ed68a1aa4467845bce17595ee4c63a9df07a)), closes [TIMSBB-315](https://jira.ti8m.ch/browse/TIMSBB-315)

## [1.9.0-develop.44](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.43...v1.9.0-develop.44) (1/19/2026)


### Features

* **mdm:** CURIAPLUS-13786 missing constructors and POST test with member attributes. ([4cd4c42](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/4cd4c423c81e1d4001b30730f3f06ed3a184fe12))

## [1.9.0-develop.43](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.42...v1.9.0-develop.43) (12/16/2025)


### Features

* **mdm:** CURIAPLUS-13786 api with get tests and a feature to validate short- and long-name individually. ([4e4c367](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/4e4c3676091e521508bc7f2d3034bc33c4976124))

## [1.9.0-develop.42](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.41...v1.9.0-develop.42) (10/15/2025)


### Features

* **persistence:** CURIAPLUS-13284 only use @Transactional for modifying methods ([92558ab](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/92558ab43051f941ee07ebc44b4a995b3b7a0e06))

## [1.9.0-develop.41](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.40...v1.9.0-develop.41) (10/15/2025)


### Features

* **reflection-proxy-unwrap:** initialize + unwrap hibernate proxy to prevent uninitialized value ([9762081](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/976208191d911fbc794fb4a79182201febd9cb5e))

## [1.9.0-develop.40](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.39...v1.9.0-develop.40) (8/23/2025)


### Features

* **command:** add app version ([15bbab9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/15bbab97209fa79d34d34bb16caf0e220b40d257))

## [1.9.0-develop.39](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.38...v1.9.0-develop.39) (8/14/2025)


### Features

* **command:** make value size adaptable ([f15e9a4](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f15e9a40e7c305148c8957c98ab0cd12c0c59458))

## [1.9.0-develop.38](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.37...v1.9.0-develop.38) (7/31/2025)


### Features

* **command:** make parent command load lazily ([94a53bb](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/94a53bb0f1c4f5029ffefe50856c58bbc7f524ab))

## [1.9.0-develop.37](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.36...v1.9.0-develop.37) (7/28/2025)


### Features

* **persistence:** add methods to query user permissions ([b25b094](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/b25b0949c0139ddb2cb4e9ee43df816828b44603))

## [1.9.0-develop.36](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.35...v1.9.0-develop.36) (7/24/2025)


### Features

* **persistence:** streamline tx handling ([0a168a6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0a168a6f69956d4b047dcd4fa1b571ce168cd431))

## [1.9.0-develop.35](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.34...v1.9.0-develop.35) (7/22/2025)


### Features

* **exception:** customize exception output through generic handler ([e0b0c7e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e0b0c7ec54013f98460a3096d8de87aea1b828c2))

## [1.9.0-develop.34](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.33...v1.9.0-develop.34) (7/22/2025)


### Features

* **rci:** throw exceptions on remote failure ([0dae2ca](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0dae2ca0f296c2e2898eacbc29cefc1ececa2a6f))

## [1.9.0-develop.33](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.32...v1.9.0-develop.33) (7/16/2025)


### Features

* **rci:** reduce excessive logging ([f29b5cc](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f29b5cc62f12a91c55ad6ca56e7d9530ce2032f0))

## [1.9.0-develop.32](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.31...v1.9.0-develop.32) (7/10/2025)


### Features

* **persistence:** dont add ids twice, already added in save() ([3b14fec](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/3b14fecf002a72f64360e15bb3358de89269adf8))

## [1.9.0-develop.31](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.30...v1.9.0-develop.31) (7/7/2025)


### Features

* **post-action:** make postaction orignal-command aware ([f3b7207](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f3b72076356aa3b3ae2706596221dcb2af8e4f0f))

## [1.9.0-develop.30](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.29...v1.9.0-develop.30) (7/4/2025)


### Features

* **command:** allow domain setting via action configuration ([5f25ddd](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5f25ddd0b898b242e30f6beac7c7d9c3a420259a))

## [1.9.0-develop.29](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.28...v1.9.0-develop.29) (7/3/2025)


### Features

* **command:** allow domain command factory to be customized ([1988d69](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1988d69d9b51faa3a976a2c52d24165575bbdfd1))

## [1.9.0-develop.28](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.27...v1.9.0-develop.28) (7/3/2025)


### Features

* **persistence:** findAllById returns empty list for empty input ([3483a4b](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/3483a4b94f987eec21eb05c59b2d18169e4c5e8d))

## [1.9.0-develop.27](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.26...v1.9.0-develop.27) (6/30/2025)


### Features

* **persistence:** update order and pagination setting to respect primary repository ([ee53db2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ee53db28c642f2a557417451ab1a9bf6bc3caac3))

## [1.9.0-develop.26](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.25...v1.9.0-develop.26) (6/27/2025)


### Features

* **persistence:** fix order/pagination calculation ([e455d63](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e455d636c3f09547e3b8d26249ec09a675d87bb8))

## [1.9.0-develop.25](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.24...v1.9.0-develop.25) (6/24/2025)


### Features

* **persistence:** ensure usage of same classloader ([ca5befa](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ca5befa682711ef549088dbafe69deffe72e4f94))

## [1.9.0-develop.24](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.23...v1.9.0-develop.24) (6/24/2025)


### Features

* **persistence:** unproxy classname in stacktrace walk ([e795dea](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e795dea174629e07c556905435f88298ea583054))

## [1.9.0-develop.23](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.22...v1.9.0-develop.23) (6/17/2025)


### Features

* **persistence:** drop manual transaction management ([1e986a5](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1e986a53880a64f59dea96fa2b6d16286515a128))

## [1.9.0-develop.22](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.21...v1.9.0-develop.22) (6/16/2025)


### Features

* **persistence:** update primary repository calculation ([011a54f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/011a54f130b5311ced6f208db2040ad567ea2391))

## [1.9.0-develop.21](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.20...v1.9.0-develop.21) (6/5/2025)


### Features

* **persistence:** ignore StreamingResponseBody in serialization ([e350962](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/e350962f1d84bb91e1be1da734df61822457a81a))

## [1.9.0-develop.20](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.19...v1.9.0-develop.20) (6/2/2025)


### Features

* **search:** make fulltext search respond to database type ([53c9bb7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/53c9bb7fae49f8487b0e07085f852ef3e7afc44e))

## [1.9.0-develop.19](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.18...v1.9.0-develop.19) (5/26/2025)


### Features

* **release:** stabalise release pipeline ([94c35d4](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/94c35d4a6bebbe6392666aa998de3fb131849912))

## [1.9.0-develop.18](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.17...v1.9.0-develop.18) (5/26/2025)


### Features

* **subscriber:** update tx logic, add configurable pool size for subscriber execution ([a399de7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/a399de7330a4ad04b5971747a6272831c0257622)), closes [TIMSBB-202](https://jira.ti8m.ch/browse/TIMSBB-202)

## [1.9.0-develop.17](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.16...v1.9.0-develop.17) (5/12/2025)


### Features

* **rci:** fix bean names ([1133fe3](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1133fe375eb9b56e1c6a417e3dae657b6e1465f0))

## [1.9.0-develop.16](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.15...v1.9.0-develop.16) (5/12/2025)


### Features

* **rci:** inject object mapper config ([8dc8972](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/8dc8972b86c6fa266810bd0287b556f706be5632))

## [1.9.0-develop.15](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.14...v1.9.0-develop.15) (5/6/2025)


### Features

* **bom:** add e-gov-luid to bom ([42a890e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/42a890e5349ded1623f71d4f3b4dfd1dd89dc8a4))

## [1.9.0-develop.14](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.13...v1.9.0-develop.14) (5/6/2025)


### Features

* **rci:** use jackson for response deserialization ([5e4f0f6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5e4f0f6cd4bc8078d0e04326bc0741079dbbb4a1)), closes [TIMSBB-180](https://jira.ti8m.ch/browse/TIMSBB-180)

## [1.9.0-develop.13](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.12...v1.9.0-develop.13) (5/1/2025)


### Features

* **mdm:** add default master data management functionalities ([53024ad](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/53024adc3987b9e5fdf704c69573cefa00c668c3)), closes [TIMSBB-170](https://jira.ti8m.ch/browse/TIMSBB-170)

## [1.9.0-develop.12](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.11...v1.9.0-develop.12) (5/1/2025)

### Features

* **mdm:** add default master data management functionalities ([13046e9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/13046e986cad2fd23e35735125bc59d56fb62100)), closes [TIMSBB-170](https://jira.ti8m.ch/browse/TIMSBB-170)

## [1.9.0-develop.11](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.10...v1.9.0-develop.11) (4/29/2025)

### Features

* **luid:** add luid generator ([2f23ace](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/2f23ace206a6a5e0500016390ffd08f15b42f58e)), closes [TIMSBB-179](https://jira.ti8m.ch/browse/TIMSBB-179)

## [1.9.0-develop.10](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.9...v1.9.0-develop.10) (4/25/2025)

### Features

* **rci:** enable header/resttemplate customization for rci ([dd9c1a9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/dd9c1a9db8f8644a0b46b8d8e59696a132ddffaf))

## [1.9.0-develop.9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.8...v1.9.0-develop.9) (4/24/2025)

### Features

* **rci:** enable custom object mapper for rest template ([782a35a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/782a35a9563731e823421a8b323e9244b72af077))

## [1.9.0-develop.8](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.7...v1.9.0-develop.8) (4/17/2025)

### Features

* **service-discovery:** add zookeeper authentication ([a96093d](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/a96093d5d5889ce7329ccc7c09ea31f29f9e5d01))

## [1.9.0-develop.7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.6...v1.9.0-develop.7) (4/16/2025)

### Features

* **persistence:** prefix table for db column names ([b84f22b](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/b84f22b2e6ce14bb0569be4ccc92aedd47f5cbdc))

## [1.9.0-develop.6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.5...v1.9.0-develop.6) (4/10/2025)

### Features

* **persistence:** switch archived filter to filter for 'not in archived=true' ([9a6e1a2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/9a6e1a29dcdd3c2ba642031fe3c5b77dca86defb))

## [1.9.0-develop.5](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.4...v1.9.0-develop.5) (4/10/2025)

### Features

* **persistence:** switch archived filter to filter for <not in archived=true> ([0a45e71](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0a45e71bafa4794af8ee4cb1affda9a9191f32de))

## [1.9.0-develop.4](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.3...v1.9.0-develop.4) (4/8/2025)

### Features

* **mdm:** stabelize additional content ([326e2f2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/326e2f234adcabcf49338c4709f7dd894606c8c0)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)

## [1.9.0-develop.3](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.2...v1.9.0-develop.3) (4/4/2025)

### Features

* **filter:** add option for db-name filter ([9f8a16a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/9f8a16a1966a9c0386cc975b4d137c576b450ae0)), closes [TIMSBB-161](https://jira.ti8m.ch/browse/TIMSBB-161)

## [1.9.0-develop.2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.9.0-develop.1...v1.9.0-develop.2) (4/4/2025)

### Features

* **service-discovery:** add zookeeper standing connection ([d4d25a6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d4d25a67f6b00f114752c1b7b2ffcaac91a9b482)), closes [TIMSBB-137](https://jira.ti8m.ch/browse/TIMSBB-137)

## [1.9.0-develop.1](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0...v1.9.0-develop.1) (4/3/2025)

### Features

* **persistence:** allow IDs to be int and long for specific methods. this is... ([722908e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/722908e458b03f306335ad014edee3170fa80959))
* **version:** update version ([f7afe9b](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f7afe9ba253d7fbd6254dbf2f6b1aaf805bf7265))

## [1.8.0-develop.19](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.18...v1.8.0-develop.19) (4/3/2025)

### Features

* **persistence:** allow IDs to be int and long for specific methods. this is... ([722908e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/722908e458b03f306335ad014edee3170fa80959))

## [1.8.0](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.7.0...v1.8.0) (4/3/2025)

### Features

* **annotations:** fix converter for validation engine: apply converters ([680e9c7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/680e9c7a79e70ed746e0b246abed4fdcf90d874c))
* **docu:** add conflucence publisher ([73059c7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/73059c77a8dc0e81bb02e6655ed5298bd83f4dc2))
* **docu:** add conflucence publisher ([000e820](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/000e820e9cce7665c4a7d3dcf958988a93a3baa1))
* **docu:** automatically add release notes to confluence ([066ff7a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/066ff7aaf54d826bf0212117d06d4b7b9280f914))
* **docu:** publish release notes to confluece ([810c773](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/810c773ff09e69244e9a387ebb97ce3c5a4befc4))
* **docu:** publish release notes to confluence ([00bd43f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/00bd43f984f8e66ccf2943df01d54f311f053c33))
* **docu:** publish release notes to confluence ([5973116](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5973116596bd82a0e171e19bb084703499e4aa84))
* **docu:** publish release notes to confluence ([0491f5e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0491f5e89bc46fb2ac0fa2b0b0343dd259839de6))
* **docu:** publish release notes to confluence ([16be4db](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/16be4dbf7326307dad0eed16834d3c81d2bce6b5))
* **docu:** publish release notes to confluence ([049110c](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/049110c0fed254907533c95f55acabf3f6bd835b))
* **docu:** publish release notes to confluence ([920bcbd](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/920bcbd1f8c3b300fe5adfd3057189db15b77b2d))
* **docu:** publish release notes to confluence ([1e6d965](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1e6d96586ff9475d042951402e8f617f1ebc4557))
* **docu:** publish release notes to confluence ([a9e3685](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/a9e368511c744ef2f0729ba7896422fad98928df))
* **docu:** publish release notes to confluence ([50ceea7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/50ceea7388cb42de08da48064ea0433efa1cf7aa))
* **docu:** publish release notes to confluence ([46c6b71](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/46c6b71bef56b1e31cf46dfb000b90a3c56d39fa))
* **docu:** publish release notes to confluence ([6f2862f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/6f2862f798c9124593b8641239016b9d9f4342d1))
* **docu:** publish release notes to confluence ([f359b42](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f359b421b473b83b5c7179e339d607c8ff93c1ba))
* **docu:** publish release notes to confluence ([0fb439f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0fb439f72f58c33ff9d8b360abd853407711f34d))
* **docu:** publish release notes to confluence ([170fce6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/170fce6cc02b59ed5e22b4a0faca508c26eae2cf))
* **docu:** publish release notes to confluence ([8018382](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/80183822e1da3e0965388fbf960e3371e3f4465f))
* **docu:** publish release notes to confluence ([75cf023](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/75cf023a2a54c8b253685e885f56ac10db8f7629))
* **docu:** publish release notes to confluence ([fb128ce](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/fb128ce8800b821ea56cb894dcb1a11d61fe7ab8))
* **docu:** publish release notes to confluence ([64aaaad](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/64aaaad367d890ae1914c3f3036398d753597775))
* **docu:** publish release notes to confluence ([d8d55a9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d8d55a9391c9ddf3ce2e5344fe4704193d4498c1))
* **docu:** publish release notes to confluence ([93bb5bd](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/93bb5bdc9749847048d214ba6b1005df2f45d247))
* **docu:** publish release notes to confluence ([d52547f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d52547f6f3e78b1fb6d1daac3063c51f88d32c20))
* **docu:** publish release notes to confluence ([61587be](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/61587be53490012c936e2705c8e578d002c15cb9))
* **masterdata:** add masterdata module ([4cd4c49](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/4cd4c49baebf59267b8355388009fb14dceaea52)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)
* **masterdata:** cleanup masterdata implementation ([d0aeec0](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d0aeec0bfc9dd1f905466b48fca21f8bc93b5eed))
* **masterdata:** escape reserved keyword ('language') for sql server ([69bf657](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/69bf6576f351ef29cb0e9deaf7c6aade4ff27885)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)
* **masterdata:** escape reserved keyword ('language') for sql server ([801db2e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/801db2e00b4ec3e229c8858bddf0f20e0585b81f)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)
* **masterdata:** escape reserved keyword ('language') for sql server ([c26e51a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/c26e51ad2300ca0caff5ee785a693066a26d7147)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)
* **version:** streamline module versions ([859eb06](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/859eb06d31372bc269b12b800a77d81bee546c0f))

### Bug Fixes

* **version:** fix version of masterdata management ([2c2fd61](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/2c2fd61250ce329c5084a580a4191d0b00f535cb))

## [1.8.0-develop.18](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.17...v1.8.0-develop.18) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([00bd43f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/00bd43f984f8e66ccf2943df01d54f311f053c33))

## [1.8.0-develop.17](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.16...v1.8.0-develop.17) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([5973116](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5973116596bd82a0e171e19bb084703499e4aa84))

## [1.8.0-develop.16](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.15...v1.8.0-develop.16) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([0491f5e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0491f5e89bc46fb2ac0fa2b0b0343dd259839de6))

## [1.8.0-develop.15](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.14...v1.8.0-develop.15) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([16be4db](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/16be4dbf7326307dad0eed16834d3c81d2bce6b5))

## [1.8.0-develop.14](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.13...v1.8.0-develop.14) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([049110c](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/049110c0fed254907533c95f55acabf3f6bd835b))

## [1.8.0-develop.13](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.12...v1.8.0-develop.13) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([920bcbd](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/920bcbd1f8c3b300fe5adfd3057189db15b77b2d))

## [1.8.0-develop.12](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.11...v1.8.0-develop.12) (4/3/2025)

### Features

* **docu:** publish release notes to confluece ([810c773](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/810c773ff09e69244e9a387ebb97ce3c5a4befc4))
* **docu:** publish release notes to confluence ([1e6d965](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/1e6d96586ff9475d042951402e8f617f1ebc4557))

## [1.8.0-develop.11](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.10...v1.8.0-develop.11) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([a9e3685](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/a9e368511c744ef2f0729ba7896422fad98928df))

## [1.8.0-develop.10](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.9...v1.8.0-develop.10) (4/3/2025)

### Features

* **docu:** publish release notes to confluence ([50ceea7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/50ceea7388cb42de08da48064ea0433efa1cf7aa))
* **docu:** publish release notes to confluence ([46c6b71](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/46c6b71bef56b1e31cf46dfb000b90a3c56d39fa))

## [1.8.0-develop.9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.8...v1.8.0-develop.9) (4/2/2025)

### Features

* **docu:** publish release notes to confluence ([6f2862f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/6f2862f798c9124593b8641239016b9d9f4342d1))
* **docu:** publish release notes to confluence ([f359b42](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/f359b421b473b83b5c7179e339d607c8ff93c1ba))
* **docu:** publish release notes to confluence ([0fb439f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/0fb439f72f58c33ff9d8b360abd853407711f34d))
* **docu:** publish release notes to confluence ([170fce6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/170fce6cc02b59ed5e22b4a0faca508c26eae2cf))
* **docu:** publish release notes to confluence ([8018382](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/80183822e1da3e0965388fbf960e3371e3f4465f))
* **docu:** publish release notes to confluence ([75cf023](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/75cf023a2a54c8b253685e885f56ac10db8f7629))
* **docu:** publish release notes to confluence ([fb128ce](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/fb128ce8800b821ea56cb894dcb1a11d61fe7ab8))
* **docu:** publish release notes to confluence ([64aaaad](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/64aaaad367d890ae1914c3f3036398d753597775))
* **docu:** publish release notes to confluence ([d8d55a9](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d8d55a9391c9ddf3ce2e5344fe4704193d4498c1))
* **docu:** publish release notes to confluence ([93bb5bd](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/93bb5bdc9749847048d214ba6b1005df2f45d247))
* **docu:** publish release notes to confluence ([d52547f](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d52547f6f3e78b1fb6d1daac3063c51f88d32c20))
* **docu:** publish release notes to confluence ([61587be](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/61587be53490012c936e2705c8e578d002c15cb9))

## [1.8.0-develop.8](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.7...v1.8.0-develop.8) (4/2/2025)

### Features

* **annotations:** fix converter for validation engine: apply converters ([680e9c7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/680e9c7a79e70ed746e0b246abed4fdcf90d874c))

## [1.8.0-develop.7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.6...v1.8.0-develop.7) (4/2/2025)

### Features

* **docu:** add conflucence publisher ([73059c7](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/73059c77a8dc0e81bb02e6655ed5298bd83f4dc2))
* **docu:** add conflucence publisher ([000e820](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/000e820e9cce7665c4a7d3dcf958988a93a3baa1))

## [1.8.0-develop.6](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.5...v1.8.0-develop.6) (4/2/2025)

### Features

* **docu:** automatically add release notes to confluence ([066ff7a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/066ff7aaf54d826bf0212117d06d4b7b9280f914))

## [1.8.0-develop.5](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.4...v1.8.0-develop.5) (4/2/2025)

### Features

* **masterdata:** escape reserved keyword ('language') for sql server ([69bf657](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/69bf6576f351ef29cb0e9deaf7c6aade4ff27885)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)
* **masterdata:** escape reserved keyword ('language') for sql server ([801db2e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/801db2e00b4ec3e229c8858bddf0f20e0585b81f)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)
* **masterdata:** escape reserved keyword ('language') for sql server ([c26e51a](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/c26e51ad2300ca0caff5ee785a693066a26d7147)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)

## [1.8.0-develop.4](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.3...v1.8.0-develop.4) (4/2/2025)

### Features

* **version:** streamline module versions ([859eb06](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/859eb06d31372bc269b12b800a77d81bee546c0f))

## [1.8.0-develop.3](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.2...v1.8.0-develop.3) (4/1/2025)

### Features

* **masterdata:** cleanup masterdata implementation ([d0aeec0](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/d0aeec0bfc9dd1f905466b48fca21f8bc93b5eed))

## [1.8.0-develop.2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.8.0-develop.1...v1.8.0-develop.2) (4/1/2025)

### Bug Fixes

* **version:** fix version of masterdata management ([2c2fd61](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/2c2fd61250ce329c5084a580a4191d0b00f535cb))

## [1.8.0-develop.1](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.7.0...v1.8.0-develop.1) (3/28/2025)

### Features

* **masterdata:** add masterdata module ([4cd4c49](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/4cd4c49baebf59267b8355388009fb14dceaea52)), closes [TIMSBB-101](https://jira.ti8m.ch/browse/TIMSBB-101)

## [1.7.0](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.6.27...v1.7.0) (3/25/2025)

### Features

* **init-pipeline:** initializes semantic versioning / conventional commits pipeline ([6c8e250](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/6c8e250a467f75b42007c7356f0150817a45b34d))

### Bug Fixes

* **init-pipeline:** disables trivy scans ([7905e30](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/7905e303ae25d8a251e4738a8eccb7d9b641b2c9)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)
* **init-pipeline:** fixes semantic release to commit changes to all poms ([fc7b017](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/fc7b0173d794c27fe5902c0e2e0df370442263d8)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)
* **init-pipeline:** removes trivy stage ([5edb070](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5edb070288e2c0e064ef2af6ecd14d3c838916d8)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)
* **init-pipeline:** resets version of root pom, should set version for all modules ([ccdf04e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ccdf04e40efd54371dc2ec2fa76b414593f66944)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)

## [1.7.0-develop.2](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.7.0-develop.1...v1.7.0-develop.2) (3/25/2025)

### Bug Fixes

* **init-pipeline:** fixes semantic release to commit changes to all poms ([fc7b017](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/fc7b0173d794c27fe5902c0e2e0df370442263d8)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)

## [1.7.0-develop.1](https://gitlab.ti8m.ch/egov-framework/framework-1.5/compare/v1.6.27...v1.7.0-develop.1) (3/25/2025)

### Features

* **init-pipeline:** initializes semantic versioning / conventional commits pipeline ([6c8e250](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/6c8e250a467f75b42007c7356f0150817a45b34d))

### Bug Fixes

* **init-pipeline:** disables trivy scans ([7905e30](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/7905e303ae25d8a251e4738a8eccb7d9b641b2c9)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)
* **init-pipeline:** removes trivy stage ([5edb070](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/5edb070288e2c0e064ef2af6ecd14d3c838916d8)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)
* **init-pipeline:** resets version of root pom, should set version for all modules ([ccdf04e](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/ccdf04e40efd54371dc2ec2fa76b414593f66944)), closes [TIMSBB-73](https://jira.ti8m.ch/browse/TIMSBB-73)

## 1.6.27 (3/25/2025)

### Features

* **init-pipeline:** initializes semantic versioning / conventional commits pipeline ([6c8e250](https://gitlab.ti8m.ch/egov-framework/framework-1.5/commit/6c8e250a467f75b42007c7356f0150817a45b34d))
